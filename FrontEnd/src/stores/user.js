import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/token'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.userInfo = res.data
      setToken(res.data.token)
      return res
    },

    async logout() {
      await logout()
      this.token = ''
      this.userInfo = null
      removeToken()
    }
  }
})
