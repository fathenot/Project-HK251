<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import logo from '@/assets/logo.png'
import SectionFullScreen from '@/components/SectionFullScreen.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import LayoutGuest from '@/layouts/LayoutGuest.vue'
import { mdiAccount, mdiEmail, mdiPhone, mdiHome, mdiAsterisk } from '@mdi/js'
import { api } from '@/plugins/axios.js'

const router = useRouter()

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  address: '',
  password: '',
  password_confirmation: '',
})

const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
const validatePhone = (phone) => /^[0-9]{10,20}$/.test(phone)

const submit = async () => {
  // Validate required fields
  if (!form.firstName || !form.lastName || !form.email || !form.password) {
    alert('Vui lòng điền đầy đủ thông tin bắt buộc!')
    return
  }

  // Email
  if (!validateEmail(form.email)) {
    alert('Email không hợp lệ!')
    return
  }

  // Password confirm
  if (form.password !== form.password_confirmation) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }

  // Phone (optional)
  if (form.phone && !validatePhone(form.phone)) {
    alert('Số điện thoại phải từ 10–20 chữ số!')
    return
  }

  // Body gửi API
  const content = {
    firstName: form.firstName,
    lastName: form.lastName,
    email: form.email,
    phone: form.phone || null,
    address: form.address || null,
    password: form.password,
  }

  try {
    const res = await api.post('/auth/register', content)

    if (res.data?.success) {
      alert('Đăng ký thành công! Vui lòng đăng nhập.')
      router.push('/login')
    } else {
      alert(res.data?.message || 'Đăng ký thất bại! Vui lòng thử lại.')
    }
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || 'Đã có lỗi xảy ra! Vui lòng thử lại.')
  }
}
</script>

<template>
  <LayoutGuest>
    <SectionFullScreen bg="softBlue">
      <CardBox
        :class="`w-11/12 rounded-xl bg-white/90 p-6 shadow-lg transition-shadow duration-300 hover:shadow-2xl sm:w-10/12 md:w-8/12 lg:w-6/12 xl:w-5/12 dark:bg-slate-900/80`"
        is-form
        @submit.prevent="submit"
      >
        <div class="mb-4 flex justify-center">
          <img :src="logo" alt="Logo" class="h-28 w-auto" />
        </div>

        <h2 class="mb-6 text-center text-2xl font-bold text-gray-800 md:text-3xl dark:text-white">
          Tạo tài khoản mới
        </h2>

        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">

          <FormField label="Họ (First Name)">
            <FormControl v-model="form.firstName" :icon="mdiAccount" />
          </FormField>

          <FormField label="Tên (Last Name)">
            <FormControl v-model="form.lastName" :icon="mdiAccount" />
          </FormField>

          <FormField label="Email">
            <FormControl
              v-model="form.email"
              :icon="mdiEmail"
              type="email"
              autocomplete="email"
            />
          </FormField>

          <FormField label="Số điện thoại (tùy chọn)">
            <FormControl
              v-model="form.phone"
              :icon="mdiPhone"
              type="tel"
              autocomplete="tel"
            />
          </FormField>

          <FormField label="Địa chỉ (tùy chọn)">
            <FormControl
              v-model="form.address"
              :icon="mdiHome"
              type="text"
              autocomplete="street-address"
            />
          </FormField>

          <FormField label="Mật khẩu">
            <FormControl
              v-model="form.password"
              :icon="mdiAsterisk"
              type="password"
              autocomplete="new-password"
            />
          </FormField>

          <FormField label="Xác nhận mật khẩu">
            <FormControl
              v-model="form.password_confirmation"
              :icon="mdiAsterisk"
              type="password"
              autocomplete="new-password"
            />
          </FormField>
        </div>

        <div class="mt-6 flex justify-center">
          <BaseButton type="submit" color="info" label="Đăng ký" class="w-full sm:w-1/2" />
        </div>

        <template #footer>
          <div class="mt-4 text-center">
            <p class="text-sm text-gray-800 dark:text-white">
              Bạn đã có tài khoản?
              <span
                class="cursor-pointer text-blue-500 hover:underline"
                @click="router.push('/login')"
              >
                Đăng nhập
              </span>
            </p>
          </div>
        </template>
      </CardBox>
    </SectionFullScreen>
  </LayoutGuest>
</template>
