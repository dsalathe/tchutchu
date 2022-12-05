import { createRouter, createWebHistory } from 'vue-router'
import GameView from '../views/GameView.vue'

const routes = [
  {
    path: '/',
    name: 'game',
    component: GameView
  },
  {
    path: '/rules',
    name: 'rules',
    // route level code-splitting
    // this generates a separate chunk (rule.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "rules" */ '../views/RulesView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
