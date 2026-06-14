import { createRouter, createWebHashHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/apply',
    children: [
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/apply/ApplyList.vue'),
        meta: { title: '陪护申请', icon: 'Document' }
      },
      {
        path: 'apply/submit',
        name: 'ApplySubmit',
        component: () => import('@/views/apply/ApplySubmit.vue'),
        meta: { title: '提交申请' }
      },
      {
        path: 'audit',
        name: 'Audit',
        component: () => import('@/views/audit/AuditList.vue'),
        meta: { title: '护士站审核', icon: 'Check' }
      },
      {
        path: 'gate',
        name: 'Gate',
        component: () => import('@/views/gate/GateCheck.vue'),
        meta: { title: '门禁查验', icon: 'Ticket' }
      },
      {
        path: 'gate/records',
        name: 'GateRecords',
        component: () => import('@/views/gate/GateRecords.vue'),
        meta: { title: '查验记录' }
      },
      {
        path: 'certificate',
        name: 'Certificate',
        component: () => import('@/views/certificate/CertificateList.vue'),
        meta: { title: '陪护证管理', icon: 'Medal' }
      },
      {
        path: 'certificate/invalid',
        name: 'CertificateInvalid',
        component: () => import('@/views/certificate/CertificateInvalid.vue'),
        meta: { title: '失效管理' }
      },
      {
        path: 'certificate/transfer',
        name: 'TransferCompanion',
        component: () => import('@/views/certificate/TransferCompanion.vue'),
        meta: { title: '换陪护管理', icon: 'Switch' }
      },
      {
        path: 'certificate/leave',
        name: 'LeaveManagement',
        component: () => import('@/views/certificate/LeaveManagement.vue'),
        meta: { title: '临时离院管理', icon: 'Position' }
      },
      {
        path: 'patient/transfer',
        name: 'WardTransfer',
        component: () => import('@/views/patient/WardTransfer.vue'),
        meta: { title: '病区转床', icon: 'Sort' }
      },
      {
        path: 'special-approval',
        name: 'SpecialApproval',
        component: () => import('@/views/audit/SpecialApprovalList.vue'),
        meta: { title: '特殊审批', icon: 'Stamp' }
      },
      {
        path: 'accompany-config',
        name: 'AccompanyConfig',
        component: () => import('@/views/ward/AccompanyConfig.vue'),
        meta: { title: '陪护人数配置', icon: 'Setting' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 医院陪护证管理系统` : '医院陪护证管理系统'
  next()
})

export default router
