<template>
  <div class="page">
    <van-cell-group inset title="后台登录">
      <van-field v-model="form.username" label="用户名" placeholder="admin" />
      <van-field v-model="form.password" label="密码" type="password" placeholder="admin" />
      <div class="btn"><van-button type="primary" block @click="login">登录</van-button></div>
    </van-cell-group>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Notify } from 'vant'
import http from '../api/http'

const router = useRouter()
const form = reactive({ username: 'admin', password: 'admin' })

const login = async () => {
  const { data } = await http.post('/api/login', form)
  if (data.success) {
    router.push('/admin/dashboard')
  } else {
    Notify({ type: 'danger', message: data.message || '登录失败' })
  }
}
</script>

<style scoped>
.page { padding: 16px; }
.btn { padding: 16px; }
</style>
