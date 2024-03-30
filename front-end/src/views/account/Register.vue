<template>
  <div class="register-container">
    <el-container class="register-wrapper">
      <el-header class="header">注 册 你 的 账 户</el-header>
      <el-container class="register-wrapper-aside">
        <el-aside class="aside">
          <el-header class="aside-info">微 信 扫 码 注 册</el-header>
          <img src="/image/common/login-wrapper-left.png" alt="Aside Image">
          <div class="msg">
            已有账号? <router-link to="/login">返回登录</router-link>
          </div>
        </el-aside>
        <el-main class="register-wrapper-form">
          <div class="register-option-container">
            <span class="register-option" :class="{ 'selected': isPhoneRegister }" @click="isPhoneRegister = true">手机注册</span>
            <span class="register-option" :class="{ 'selected': !isPhoneRegister }" @click="isPhoneRegister = false">邮箱注册</span>
          </div>
          <el-form
              v-if="isPhoneRegister"
              ref="registerForm"
              :model="phoneRegisterForm"
              :rules="phoneRegisterRules"
              label-width="0px"
              @submit.native.prevent="submitPhoneForm"
          >
            <div class="account-input">
              <el-form-item  prop="account">
                <el-input v-model="phoneRegisterForm.username" placeholder="请输入用户名" class="input-item" >
                  <template #prefix>
                    <img src="/image/icon/account.svg" alt="account icon" class="input-icon"/>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="phoneRegisterForm.password" type="password" placeholder="请输入密码" class="input-item">
                  <template #prefix>
                    <img src="/image/icon/password.svg" alt="password icon" class="input-icon"/>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="phone">
                <el-input v-model="phoneRegisterForm.phone" placeholder="请输入手机号码" class="input-item">
                  <template #prefix>
                    <img src="/image/icon/phone.svg" alt="phone icon" class="input-icon"/>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="code">
                <div class="code">
                  <el-input v-model="phoneRegisterForm.code" placeholder="请输入验证码" class="input-item">
                    <template #prefix>
                      <img src="/image/icon/code.svg" alt="code icon" class="input-icon"/>
                    </template>
                  </el-input>
                  <el-button type="primary" @click="sendPhoneCode" :disabled="isSending" :class="{ 'disabled-button': isSending }" class="input-item">
                    {{ isSending ? `${countdown}秒后重新发送` : '发送验证码' }}
                  </el-button>
                </div>
              </el-form-item>
            </div>
            <el-form-item class="privacy">
              <el-checkbox v-model="agreedToTerms">我已阅读并接受
                <router-link to="/terms-of-service" target="_blank">量子论坛用户协议</router-link> 和
                <router-link to="/privacy-policy" target="_blank">隐私政策</router-link>
              </el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" class="register-button">注册</el-button>
            </el-form-item>
          </el-form>
          <el-form
              v-else
              ref="emailRegisterForm"
              :model="emailRegisterForm"
              :rules="emailRegisterRules"
              label-width="0px"
              @submit.native.prevent="submitEmailForm"
          >
            <el-form-item  prop="account">
              <el-input v-model="emailRegisterForm.username" placeholder="请输入用户名" class="input-item">
                <template #prefix>
                  <img src="/image/icon/account.svg" alt="account icon" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="emailRegisterForm.password" type="password" placeholder="请输入密码" class="input-item">
                <template #prefix>
                  <img src="/image/icon/password.svg" alt="password icon" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="emailRegisterForm.email" placeholder="请输入邮箱" class="input-item">
                <template #prefix>
                  <img src="/image/icon/email.svg" alt="email icon" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="smsCode">
              <div class="code">
                <el-input v-model="emailRegisterForm.code" placeholder="请输入验证码" class="input-item" >
                  <template #prefix>
                    <img src="/image/icon/code.svg" alt="code icon" class="input-icon" />
                  </template>
                </el-input>
                <el-button type="primary" @click="sendEmailCode" :disabled="isSending" :class="{ 'disabled-button': isSending }" class="input-item">
                  {{ isSending ? `${countdown}秒后重新发送` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item class="privacy">
              <el-checkbox v-model="agreedToTerms">我已阅读并接受
                <router-link to="/terms-of-service" target="_blank">量子论坛用户协议</router-link> 和
                <router-link to="/privacy-policy" target="_blank">隐私政策</router-link>
              </el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" class="register-button">注册</el-button>
            </el-form-item>
          </el-form>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import axios from 'axios';

export default {
  data() {
    return {
      isPhoneRegister: true, // 默认为手机号注册
      agreedToTerms: false,
      phoneRegisterForm: {
        password: '',
        username: '',
        phone: '',
        code: '',
      },

      phoneRegisterRules: {
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
      },

      emailRegisterForm: {
        username: '',
        password: '',
        email: '',
        code: ''
      },

      emailRegisterRules: {
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
      },
      countdown:0,
      isSending:false,
    };
  },

  methods: {
    sendEmailCode(){
      axios.post("http://localhost:8080/auth/send-email",{
        email:this.emailRegisterForm.email,
        subject:'您的登录验证码已发送，请在5分钟内使用,如非本人操作，请忽略本邮件。',
        to:this.emailRegisterForm.email
      })
          .then(response=>{
              console.log(response);
              this.startCountdown();
          }).catch(error=> {
        console.error(error);
      });
    },

    submitPhoneForm() {
      this.$refs.phoneRegisterForm.validate(valid => {
        if (valid) {
          axios.post('http://localhost:8080/auth/register-by-phone', this.phoneRegisterForm)
              .then(response => {
                if (response.data.statusCode === 200) {
                  this.$message.success('注册成功，5秒后跳转到登录页面');
                  setTimeout(() => {
                    this.$router.push('/login');
                  }, 5000);
                }
              })
              .catch(error => {
                console.error(error);
              });
        }
      });
    },

    submitEmailForm() {
      this.$refs.emailRegisterForm.validate(valid => {
        if (valid) {
          axios.post('http://localhost:8080/auth/register-by-email', {
            account:{
              email: this.emailRegisterForm.email,
              password: this.emailRegisterForm.password,
              username: this.emailRegisterForm.username,
              code: this.emailRegisterForm.code,
            }
          })
              .then(response => {
                if (response.data.statusCode === 200) {
                  this.$message.success('注册成功，5秒后跳转到登录页面');
                  setTimeout(() => {
                    this.$router.push('/login');
                  }, 5000);
                }
              })
              .catch(error => {
                console.error(error);
              });
        }
      });
    },

    startCountdown() {
      this.countdown = 60;
      this.isSending = true;
      const timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          this.isSending = false;
          clearInterval(timer);
        }
      }, 1000);
    },

  }
};
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('public/image/common/register-background.jpg');
  background-size: cover;
}

.register-wrapper {
  background-color: rgba(255, 255, 255, 0.8);
  width: 100%;
  max-width: 1000px;
  height: 700px;
  border-radius: 30px;
  margin: auto;
}

.header {
  font-size: 38px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 0;
  background-image: url("/image/common/auth-banner.png");
  height: auto;
  padding: 20px;
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
  color: linen;
}

.input-item {
  margin-bottom: 10px;
  height: 55px;
}

.disabled-button {
  background-color: #ccc !important;
  border-color: #ccc !important;
  cursor: not-allowed !important;
}

.msg {
  padding-left: 20px;
  margin-top: 20px;
  font-size: 20px;
}

.register-link {
  color: #abc1ee;
  cursor: pointer;
  text-decoration: none;
}

.register-wrapper-form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-left: 10px;
  margin-top: -30px;
}

.register-wrapper-aside {
  display: flex;
  justify-content: center; /* 设置justify-content为center */
  align-items: center; /* 设置align-items为center */
  height: 100%;
  margin: 20px 30px 10px 20px;
}

.aside {
  width: 50%; /* 设置宽度为80% */
  height: 100%;
  object-fit: cover;
  border-bottom-left-radius: 30px;
}

.aside img {
  margin-top: 0;
  width: 80%;
  height: 60%; /* 设置高度为100% */
  object-fit: cover; /* 图片保持宽高比，同时填充元素的整个内容框 */
  border-radius: 30px;
  align-items: center;
  padding: 30px;
  background: transparent;
}

.account-input {
  width: 100%;
  height: auto;
  border-radius: 30px;
}

.aside-info {
  font-size: 24px;
  font-weight: bold;
  text-align: left;
  margin-bottom: 20px;
  background-color: rgba(240, 240, 240, 0);
  padding: 20px;
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
}

.register-option {
  cursor: pointer;
  color: gray;
  font-size: 24px;
  margin: 20px 30px;
}

.register-option.selected {
  font-weight: bold;
  color: black;
  border-bottom: 2px solid blue;
}

.register-option-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.register-button{
  width: 100%;
  height: 55px;
  border-radius: 10px;
  margin-top: 20px;
  font-size: 24px;
  font-weight: bold;
  background-color: #1684ff;
  color: white;
}

.privacy{
  margin-top: 20px;
  font-size: 16px;
  font-weight: bold;
}

.code{
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.input-icon{
  width: 30px;
  height:30px;
}
</style>
