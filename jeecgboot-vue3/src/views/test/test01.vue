<template>
  <div>
    <h1>DeepSeek Chat</h1>
    <input v-model="userMessage" placeholder="输入你的消息" />
    <button @click="sendMessage">发送</button>
    <div v-if="response">
      <h3>AI 回复:</h3>
      <p>{{ response }}</p>
    </div>
  </div>
</template>

<script>
import { chat } from './api'; // 引入 chat 方法

export default {
  data() {
    return {
      userMessage: '',
      response: ''
    };
  },
  methods: {
    async sendMessage() {
      try {
        // 调用 chat 方法发送消息
        const result = await chat(this.userMessage);
        console.log('result:', result);
        this.response = result; // 设置 AI 回复
        
      } catch (error) {
        console.error('Error:', error);
        this.response = '请求失败，请重试。';
      }
    }
  }
};
</script>