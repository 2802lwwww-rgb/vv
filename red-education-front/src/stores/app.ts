import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    const sidebarOpened = ref(true)
    const loading = ref(false)

    function toggleSidebar() {
        sidebarOpened.value = !sidebarOpened.value
    }

    function setLoading(value: boolean) {
        loading.value = value
    }

    return {
        sidebarOpened,
        loading,
        toggleSidebar,
        setLoading
    }
})
