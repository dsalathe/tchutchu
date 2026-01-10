import Keycloak from 'keycloak-js'

const keycloakConfig = {
  url: process.env.VUE_APP_KEYCLOAK_URL || 'http://localhost:8180',
  realm: process.env.VUE_APP_KEYCLOAK_REALM || 'dsalathe-apps',
  clientId: process.env.VUE_APP_KEYCLOAK_CLIENT_ID || 'tchutchu'
}

// Singleton Keycloak instance
let keycloakInstance = null
let keycloakInitialized = false

export function getKeycloak () {
  if (!keycloakInstance) {
    keycloakInstance = new Keycloak(keycloakConfig)
  }
  return keycloakInstance
}

// Token refresh interval handle
let refreshIntervalId = null

// Refresh token when it expires in less than 60 seconds
const MIN_VALIDITY_SECONDS = 60
// Check every 30 seconds
const REFRESH_CHECK_INTERVAL_MS = 30 * 1000

/**
 * Check if we're returning from a Keycloak login redirect
 */
function isLoginCallback () {
  const hash = window.location.hash
  const search = window.location.search
  return hash.includes('code=') || hash.includes('state=') ||
         search.includes('code=') || search.includes('state=')
}

/**
 * Initialize Keycloak silently (check-sso).
 * Does NOT force login - just checks if user is already authenticated.
 */
export async function initKeycloakSilent () {
  const keycloak = getKeycloak()

  if (keycloakInitialized) {
    return keycloak.authenticated
  }

  // If returning from login redirect, don't use check-sso
  const isCallback = isLoginCallback()

  const initOptions = {
    onLoad: isCallback ? undefined : 'check-sso',
    checkLoginIframe: true,
    checkLoginIframeInterval: 5,
    pkceMethod: 'S256',
    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    enableLogging: process.env.NODE_ENV === 'development'
  }

  // Set up event handlers for SSO session changes
  keycloak.onAuthLogout = () => {
    // User logged out from another app in the realm (SLO)
    console.log('Session ended (logged out from another app)')
    stopTokenRefresh()
    // Dispatch a custom event so the app can react
    window.dispatchEvent(new CustomEvent('keycloak-logout'))
  }

  keycloak.onTokenExpired = () => {
    console.log('Token expired, attempting refresh...')
    keycloak.updateToken(MIN_VALIDITY_SECONDS).catch(() => {
      console.log('Failed to refresh expired token')
    })
  }

  try {
    // Set a timeout to prevent hanging if Keycloak is unavailable
    const timeoutPromise = new Promise((resolve, reject) => {
      setTimeout(() => reject(new Error('Keycloak init timeout')), 5000)
    })

    const authenticated = await Promise.race([
      keycloak.init(initOptions),
      timeoutPromise
    ])
    keycloakInitialized = true

    if (authenticated) {
      startTokenRefresh()
      // Clean up the URL after successful authentication
      if (isCallback) {
        window.history.replaceState({}, document.title, window.location.pathname)
      }
    }

    return authenticated
  } catch (error) {
    console.error('Keycloak initialization failed:', error)
    keycloakInitialized = true // Mark as initialized even on failure to prevent retry loops
    return false
  }
}

/**
 * Trigger login flow.
 */
export async function login () {
  const keycloak = getKeycloak()

  // Initialize Keycloak if not already done
  if (!keycloakInitialized) {
    try {
      await keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false,
        pkceMethod: 'S256'
      })
    } catch (error) {
      console.error('Keycloak init for login failed:', error)
    }
  } else {
    // Already initialized, just trigger login
    keycloak.login({
      redirectUri: window.location.origin + '/'
    })
  }
}

/**
 * Start background token refresh to keep session alive during long games.
 */
export function startTokenRefresh () {
  stopTokenRefresh()

  const keycloak = getKeycloak()

  refreshIntervalId = setInterval(async () => {
    if (keycloak.authenticated) {
      try {
        const refreshed = await keycloak.updateToken(MIN_VALIDITY_SECONDS)
        if (refreshed) {
          console.debug('Token refreshed successfully')
        }
      } catch (error) {
        console.error('Failed to refresh token:', error)
      }
    }
  }, REFRESH_CHECK_INTERVAL_MS)
}

/**
 * Stop background token refresh.
 */
export function stopTokenRefresh () {
  if (refreshIntervalId !== null) {
    clearInterval(refreshIntervalId)
    refreshIntervalId = null
  }
}

/**
 * Get the current valid access token, refreshing if necessary.
 */
export async function getAccessToken () {
  const keycloak = getKeycloak()

  if (!keycloak.authenticated) {
    return undefined
  }

  try {
    await keycloak.updateToken(MIN_VALIDITY_SECONDS)
    return keycloak.token
  } catch (error) {
    console.error('Failed to get valid access token:', error)
    return undefined
  }
}

/**
 * Logout the user.
 * Uses Keycloak's end session endpoint with id_token_hint for proper Single Logout (SLO).
 */
export function logout () {
  stopTokenRefresh()
  const keycloak = getKeycloak()

  // Clear local session state
  keycloakInitialized = false

  // Use Keycloak logout which calls the end_session_endpoint
  // This will properly logout from Keycloak and all apps in the realm (SLO)
  keycloak.logout({
    redirectUri: window.location.origin + '/',
    // id_token_hint is automatically included by keycloak-js when available
  })
}

/**
 * Get user info from the ID token.
 */
export function getUserInfo () {
  const keycloak = getKeycloak()

  if (!keycloak.authenticated || !keycloak.idTokenParsed) {
    return null
  }

  const idToken = keycloak.idTokenParsed

  const realmRoles = keycloak.realmAccess?.roles || []
  const clientRoles = keycloak.resourceAccess?.[keycloakConfig.clientId]?.roles || []
  const roles = [...new Set([...realmRoles, ...clientRoles])]

  // Determine tier from roles or token claims
  let tier = 'basic'
  if (roles.includes('premium-tchutchu') || roles.includes('super-premium') || idToken.tier === 'all' || idToken.tier === 'single') {
    tier = 'premium'
  }

  return {
    id: idToken.sub,
    name: idToken.name || idToken.given_name || idToken.preferred_username,
    email: idToken.email,
    preferredUsername: idToken.preferred_username,
    picture: idToken.picture,
    roles,
    tier,
    tierStatus: idToken.tier_status,
    tierExpires: idToken.tier_expires
  }
}

/**
 * Check if user has a specific role.
 */
export function hasRole (role) {
  const userInfo = getUserInfo()
  return userInfo?.roles.includes(role) || false
}

/**
 * Check if user has basic access role.
 */
export function isAuthorizedUser () {
  return hasRole('user')
}

/**
 * Check if user has premium access.
 */
export function isPremiumUser () {
  return hasRole('premium-tchutchu') || hasRole('super-premium')
}

/**
 * Check if Keycloak is initialized.
 */
export function isInitialized () {
  return keycloakInitialized
}

/**
 * Check if user is authenticated.
 */
export function isAuthenticated () {
  const keycloak = getKeycloak()
  return keycloak.authenticated || false
}
