<script setup>
import { reactive } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import logo from '@/assets/logo.png'
import SectionFullScreen from '@/components/SectionFullScreen.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import LayoutGuest from '@/layouts/LayoutGuest.vue'
import { mdiAccount, mdiAsterisk } from '@mdi/js'
import { useMainStore } from '@/stores/main'

const router = useRouter()
const mainStore = useMainStore()

const form = reactive({
  username: '',
  password: '',
})

const submit = async () => {
  try {
    const success = await mainStore.login(form.username, form.password)

    if (success) {
      switch (mainStore.role) {
        case 'admin':
          router.push('/admin-dashboard')
          break
        case 'manager':
          router.push('/manager-dashboard')
          break
        case 'sales':
          router.push('/sales-dashboard')
          break
        case 'warehouse':
          router.push('/warehouse-dashboard')
          break
        case 'customer':
          router.push('/customer-dashboard')
          break
        default:
          router.push('/customer-dashboard')
      }
    }
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <LayoutGuest>
    <SectionFullScreen bg="softBlue">
      <CardBox
        :class="`w-11/12 rounded-xl bg-white/90 p-4 shadow-lg transition-shadow duration-300 hover:shadow-2xl sm:w-8/12 md:w-6/12 lg:w-5/12 xl:w-3/12 dark:bg-slate-900/80`"
        is-form
        @submit.prevent="submit"
      >
        <div class="mb-4 flex justify-center">
          <img :src="logo" alt="Logo" class="h-30 w-auto" />
        </div>
        <h2 class="mb-6 text-center text-2xl font-bold text-gray-800 md:text-3xl dark:text-white">
          Đăng nhập
        </h2>

        <FormField label="Tên đăng nhập" help="Nhập tên đăng nhập hoặc email">
          <FormControl
            v-model="form.username"
            :icon="mdiAccount"
            name="login"
            autocomplete="username"
          />
        </FormField>

        <FormField label="Mật khẩu" help="Nhập mật khẩu tài khoản">
          <FormControl
            v-model="form.password"
            :icon="mdiAsterisk"
            type="password"
            name="password"
            autocomplete="current-password"
          />
        </FormField>

        <div class="-mt-8 mb-4 text-right">
          <!-- <RouterLink to="/reset-password" class="text-sm text-blue-500 hover:underline">
            Quên mật khẩu?
          </RouterLink> -->
          <span class="text-sm text-blue-500 hover:underline">Quên mật khẩu?</span>
        </div>

        <div class="flex justify-center">
          <BaseButton type="submit" color="info" label="Đăng nhập" class="w-full" />
        </div>

        <template #footer>
          <div class="text-center">
            <p class="text-sm text-gray-800 dark:text-white">
              Bạn chưa có tài khoản?
              <RouterLink to="/register" class="text-sm text-blue-500 hover:underline">
                Tạo tài khoản mới
              </RouterLink>
            </p>
          </div>
        </template>
      </CardBox>
    </SectionFullScreen>
  </LayoutGuest>
</template>
