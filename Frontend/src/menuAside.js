import {
  mdiMonitor,
  mdiClipboardPlus,
  mdiClipboardCheck,
  mdiDatabaseSearch,
  mdiPackageVariant,
  mdiArrowRight,
  mdiWarehouse,
  mdiStore,
  mdiAccountGroup,
  mdiAccount,
  mdiTag,
} from '@mdi/js'

export default {
  admin: [],

  manager: [
    { to: '/manager-dashboard', icon: mdiMonitor, label: 'Trang chính' },
    {
      label: 'Tra cứu',
      icon: mdiClipboardCheck,
      menu: [
        { to: '/search-employee', icon: mdiAccountGroup, label: 'Nhân viên' },
        { to: '/search-product', icon: mdiPackageVariant, label: 'Lô hàng/Hàng hóa' },
        { to: '/search-customer', icon: mdiAccount, label: 'Khách hàng' },
      ],
    },
    { to: '/store-info', icon: mdiStore, label: 'Thông tin gian hàng' },
    { to: '/discount-management', icon: mdiTag, label: 'Quản lý ưu đãi giảm giá' },
  ],

  warehouse: [
    { to: '/warehouse-dashboard', icon: mdiMonitor, label: 'Trang chính' },
    { to: '/create-receipt', icon: mdiClipboardPlus, label: 'Tạo phiếu nhập/xuất' },
    {
      label: 'Tra cứu',
      icon: mdiDatabaseSearch,
      menu: [
        { to: '/search-receipt', icon: mdiClipboardCheck, label: 'Phiếu nhập/xuất' },
        { to: '/search-product', icon: mdiPackageVariant, label: 'Lô hàng/Hàng hóa' },
      ],
    },
    { to: '/transfer', icon: mdiArrowRight, label: 'Chuyển hàng' },
    { to: '/warehouses', icon: mdiWarehouse, label: 'Quản lí kho' },
    { to: '/store-info', icon: mdiStore, label: 'Thông tin gian hàng' },
  ],

  sales: [],

  customer: [],
}
