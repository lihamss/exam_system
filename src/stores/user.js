import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    name: localStorage.getItem('name') || '',
    role: localStorage.getItem('role') || '',
    token: localStorage.getItem('token') || '',
  }),
  getters: {
    isLogin: (state) => !!state.name && !!state.role,
  },
  actions: {
    login(name, role, token) {
      this.name = name
      this.role = role
      this.token = token
      localStorage.setItem('name', name)
      localStorage.setItem('role', role)
      localStorage.setItem('token', token)
    },
    logout() {
      this.name = ''
      this.role = ''
      this.token = ''
      localStorage.removeItem('name')
      localStorage.removeItem('role')
      localStorage.removeItem('token')
    },
  },
})
