<script setup>
import { reactive, ref } from 'vue'
import { mdiAccount, mdiMail, mdiPhone, mdiHome, mdiCalendar, mdiLock } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModalNew.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useMainStore } from '@/stores/main'
import { api } from '@/plugins/axios.js'

const mainStore = useMainStore()

const profileForm = reactive({})

if (mainStore.userName) profileForm.name = mainStore.userName
if (mainStore.userEmail) profileForm.email = mainStore.userEmail
if (mainStore.userPhone) profileForm.phone = mainStore.userPhone
if (mainStore.userAddress) profileForm.address = mainStore.userAddress
if (mainStore.hireDate) profileForm.hireDate = mainStore.hireDate
if (mainStore.points && mainStore.role === 'customer') profileForm.points = mainStore.points

// Form đổi mật khẩu
const passwordForm = reactive({
  password_current: '',
  password: '',
  password_confirmation: '',
})

// Modal thông báo chung
const modalActive = ref(false)
const modalMessage = ref('')
const modalTitle = ref('Thông báo')

// Modal đổi mật khẩu
const editPasswordMode = ref(false)

const showModal = (title, message) => {
  modalTitle.value = title
  modalMessage.value = message
  modalActive.value = true
}

const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

const submitProfile = async () => {
  if (!profileForm.name) {
    showModal('Lỗi', 'Họ và tên là bắt buộc!')
    return
  }

  if (mainStore.role === 'customer') {
    if (!profileForm.email) {
      showModal('Lỗi', 'Email là bắt buộc!')
      return
    }
    if (!validateEmail(profileForm.email)) {
      showModal('Lỗi', 'Email không hợp lệ!')
      return
    }
  } else {
    if (profileForm.email && !validateEmail(profileForm.email)) {
      showModal('Lỗi', 'Email không hợp lệ!')
      return
    }
  }

  if (profileForm.phone && !/^\d{9,15}$/.test(profileForm.phone)) {
    showModal('Lỗi', 'Số điện thoại không hợp lệ!')
    return
  }

  try {
    const payload = { ...profileForm }

    const res = await api.put('/user/profile', payload)

    if (!res.data?.success) {
      showModal('Lỗi', res.data?.message || 'Cập nhật thất bại!')
      return
    }

    mainStore.setUser(payload)
    showModal('Thành công', res.data?.message || 'Thông tin đã được cập nhật!')
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Cập nhật thất bại!')
  }
}

const submitPassword = async () => {
  // Validation
  if (
    !passwordForm.password_current ||
    !passwordForm.password ||
    !passwordForm.password_confirmation
  ) {
    showModal('Lỗi', 'Vui lòng điền đầy đủ các trường mật khẩu!')
    return
  }

  if (passwordForm.password !== passwordForm.password_confirmation) {
    showModal('Lỗi', 'Mật khẩu xác nhận không khớp!')
    return
  }

  try {
    const res = await api.put('/user/password', { ...passwordForm })

    if (!res.data?.success) {
      showModal('Lỗi', res.data?.message || 'Đổi mật khẩu thất bại!')
      return
    }

    showModal('Thành công', res.data?.message || 'Đổi mật khẩu thành công!')
    passwordForm.password_current = ''
    passwordForm.password = ''
    passwordForm.password_confirmation = ''
    editPasswordMode.value = false
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Đổi mật khẩu thất bại!')
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain class="flex flex-col gap-6 lg:flex-row">
      <div class="flex-shrink-0">
        <img
          :src="mainStore.userAvatar"
          alt="avatar"
          class="h-32 w-32 rounded-full border shadow-sm"
        />
      </div>

      <div class="flex-1">
        <CardBox>
          <FormField v-if="profileForm.name !== undefined" label="Họ và tên">
            <FormControl v-model="profileForm.name" :icon="mdiAccount" />
          </FormField>

          <FormField v-if="profileForm.email !== undefined" label="Email">
            <FormControl v-model="profileForm.email" :icon="mdiMail" />
          </FormField>

          <FormField v-if="profileForm.phone !== undefined" label="Số điện thoại">
            <FormControl v-model="profileForm.phone" :icon="mdiPhone" />
          </FormField>

          <FormField v-if="profileForm.address !== undefined" label="Địa chỉ">
            <FormControl v-model="profileForm.address" :icon="mdiHome" />
          </FormField>

          <FormField v-if="profileForm.hireDate !== undefined" label="Ngày vào làm">
            <FormControl v-model="profileForm.hireDate" :icon="mdiCalendar" />
          </FormField>

          <FormField v-if="profileForm.points !== undefined" label="Điểm tích lũy">
            <FormControl :value="profileForm.points" readonly />
          </FormField>

          <template #footer>
            <BaseButtons class="mt-4">
              <BaseButton color="success" label="Lưu thông tin" @click="submitProfile" />
              <BaseButton color="info" label="Đổi mật khẩu" @click="editPasswordMode = true" />
            </BaseButtons>
          </template>
        </CardBox>
      </div>
    </SectionMain>

    <CardBoxModal v-model="editPasswordMode" title="Đổi mật khẩu">
      <FormField label="Mật khẩu hiện tại">
        <FormControl v-model="passwordForm.password_current" type="password" :icon="mdiLock" />
      </FormField>
      <FormField label="Mật khẩu mới">
        <FormControl v-model="passwordForm.password" type="password" :icon="mdiLock" />
      </FormField>
      <FormField label="Xác nhận mật khẩu">
        <FormControl v-model="passwordForm.password_confirmation" type="password" :icon="mdiLock" />
      </FormField>

      <div class="mt-10">
        <BaseButtons>
          <BaseButton color="success" label="Đổi mật khẩu" @click="submitPassword" />
        </BaseButtons>
      </div>
    </CardBoxModal>

    <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK">
      <p>{{ modalMessage }}</p>
    </CardBoxModal>
  </LayoutAuthenticated>
</template>
