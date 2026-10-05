<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'

const mobile = ref('')
const otp = ref('')
const busy = ref(false)
const sent = ref(false)
const seconds = ref(0)
const status = ref({ type: '', text: '' })
let timer

const canSend = computed(() => /^\d{5,20}$/.test(mobile.value) && seconds.value === 0 && !busy.value)
const canLogin = computed(() => /^\d{5,20}$/.test(mobile.value) && /^\d{6}$/.test(otp.value) && sent.value && !busy.value)

function message(type, text) { status.value = { type, text } }

async function request(path, payload) {
  const response = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok || body.success === false) throw new Error(body.message || '请求未完成')
  return body
}

async function sendOtp() {
  if (!canSend.value) return
  busy.value = true
  message('', '')
  try {
    await request('/api/opt-send', { otp_channel_no: mobile.value })
    sent.value = true
    seconds.value = 60
    timer = window.setInterval(() => {
      seconds.value -= 1
      if (seconds.value <= 0) window.clearInterval(timer)
    }, 1000)
    message('success', '验证码已发送，请输入短信中的 6 位数字')
  } catch (error) {
    message('error', error.message)
  } finally { busy.value = false }
}

async function startCapture() {
  if (!canLogin.value) return
  busy.value = true
  message('', '')
  try {
    await request('/api/opt-login', { mobile: mobile.value, otp: otp.value })
    message('success', '登录请求已完成，可以继续处理后续响应')
  } catch (error) {
    message('error', error.message)
  } finally { busy.value = false }
}

onBeforeUnmount(() => window.clearInterval(timer))
</script>

<template>
  <main class="page-shell">
    <section class="login-panel" aria-labelledby="page-title">
      <div class="brand-mark">FGO / REQUEST LAB</div>
      <h1 id="page-title">验证码登录</h1>
      <p class="subtitle">建立一次登录会话，继续分析后续请求响应</p>

      <form @submit.prevent="startCapture">
        <label for="mobile">手机号</label>
        <div class="input-row">
          <input id="mobile" v-model.trim="mobile" inputmode="tel" autocomplete="tel" placeholder="请输入手机号" maxlength="20" />
          <button class="send-button" type="button" :disabled="!canSend" @click="sendOtp">
            <span v-if="seconds">{{ seconds }}s</span><span v-else>发送验证码</span>
          </button>
        </div>

        <label for="otp">短信验证码</label>
        <input id="otp" v-model.trim="otp" inputmode="numeric" autocomplete="one-time-code" placeholder="输入 6 位验证码" maxlength="6" @input="otp = otp.replace(/\D/g, '').slice(0, 6)" />

        <button class="primary-button" type="submit" :disabled="!canLogin">
          <span class="button-dot"></span>{{ busy ? '处理中…' : '开始抓包' }}
        </button>
      </form>

      <p v-if="status.text" class="status" :class="status.type">{{ status.text }}</p>
      <p class="footnote">验证码发送记录仅在服务端内存中暂存 5 分钟</p>
    </section>
  </main>
</template>
