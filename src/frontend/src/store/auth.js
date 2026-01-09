import {
  initKeycloakSilent,
  login as keycloakLogin,
  logout as keycloakLogout,
  getUserInfo,
  isAuthenticated
} from '@/services/keycloak'

const state = {
  initialized: false,
  user: null
}

const getters = {
  isAuthenticated: (state) => state.user !== null,
  user: (state) => state.user,
  userName: (state) => state.user?.name || state.user?.preferredUsername || null,
  userPicture: (state) => state.user?.picture || null,
  userTier: (state) => state.user?.tier || 'basic',
  isPremium: (state) => state.user?.tier === 'premium',
  isInitialized: (state) => state.initialized
}

const actions = {
  async initAuth ({ commit }) {
    try {
      const authenticated = await initKeycloakSilent()
      if (authenticated) {
        const userInfo = getUserInfo()
        commit('SET_USER', userInfo)
      }
      commit('SET_INITIALIZED', true)
      return authenticated
    } catch (error) {
      console.error('Failed to initialize auth:', error)
      commit('SET_INITIALIZED', true)
      return false
    }
  },

  async login () {
    await keycloakLogin()
  },

  logout ({ commit }) {
    commit('SET_USER', null)
    keycloakLogout()
  },

  refreshUserInfo ({ commit }) {
    if (isAuthenticated()) {
      const userInfo = getUserInfo()
      commit('SET_USER', userInfo)
    }
  }
}

const mutations = {
  SET_USER (state, user) {
    state.user = user
  },
  SET_INITIALIZED (state, initialized) {
    state.initialized = initialized
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
