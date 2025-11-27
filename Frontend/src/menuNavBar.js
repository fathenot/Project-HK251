import { mdiAccount, mdiLogout, mdiThemeLightDark, mdiBell } from '@mdi/js'

export default {
  admin: [],
  manager: [
    {
      icon: mdiBell,
      label: 'Thông báo',
      isDesktopNoLabel: true,
    },
    {
      isCurrentUser: true,
      menu: [
        {
          icon: mdiAccount,
          label: 'Tài khoản',
          to: '/profile',
        },
        {
          isDivider: true,
        },
        {
          icon: mdiLogout,
          label: 'Đăng xuất',
          isLogout: true,
        },
      ],
    },
  ],
  warehouse: [
    {
      icon: mdiBell,
      label: 'Thông báo',
      isDesktopNoLabel: true,
    },
    {
      isCurrentUser: true,
      menu: [
        {
          icon: mdiAccount,
          label: 'Tài khoản',
          to: '/profile',
        },
        {
          isDivider: true,
        },
        {
          icon: mdiLogout,
          label: 'Đăng xuất',
          isLogout: true,
        },
      ],
    },
  ],
  sales: [],
  customer: [],
}
