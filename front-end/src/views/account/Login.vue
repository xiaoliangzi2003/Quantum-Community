<template>
  <div class="login-container">
    <el-container class="login-wrapper">
      <el-header class="header">登 录 后 内 容 更 精 彩</el-header>
      <el-container class="login-wrapper-aside">
        <el-aside class="aside">
          <el-header class="aside-info">微 信 扫 描 二 维 码 登 录</el-header>
          <img src="/image/common/login-wrapper-left.png" alt="Aside Image">
          <div class="msg">
            没有账号? <router-link to="/register">点击注册</router-link>
          </div>
        </el-aside>
        <el-main class="login-wrapper-form">
          <div class="login-option-container">
            <span class="login-option" :class="{ 'selected': isAccountLogin }" @click="isAccountLogin = true">账号登录</span>
            <span class="login-option" :class="{ 'selected': !isAccountLogin }" @click="isAccountLogin = false">短信登录</span>
          </div>
          <el-form
              v-if="isAccountLogin"
              ref="loginForm"
              :model="loginForm"
              :rules="loginRules"
              label-width="0px"
              @submit.native.prevent="submitForm"
          >
            <div class="account-input">
              <el-form-item  prop="account">
                <el-input v-model="loginForm.account" placeholder="请输入用户名/邮箱/密码" class="input-item" >
                  <template #prefix>
                    <img src="/image/icon/account.svg" alt="account icon" class="input-icon"/>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" class="input-item" >
                  <template #prefix>
                    <img src="/image/icon/password.svg" alt="password icon" class="input-icon"/>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="captcha">
                <div class="captcha">
                  <el-input v-model="loginForm.captcha" placeholder="请输入验证码" class="input-item" >
                    <template #prefix>
                      <img src="/image/icon/code.svg" alt="code icon" class="input-icon"/>
                    </template>
                  </el-input>
                  <img class="captcha-image" :src="captchaImage" alt="验证码" @click="refreshCaptcha">
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
              <el-button type="primary" native-type="submit" class="login-button">登录</el-button>
            </el-form-item>
          </el-form>
          <el-form
              v-else
              ref="smsLoginForm"
              :model="smsLoginForm"
              :rules="smsLoginRules"
              label-width="0px"
              @submit.native.prevent="submitSmsForm"
          >
          <el-form-item prop="account">
            <el-input v-model="smsLoginForm.account" placeholder="请输入手机号/邮箱" class="input-item">
              <template #prefix>
                <img src="/image/icon/account.svg" alt="account icon" class="input-icon"/>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="smsCode">
            <div class="captcha">
              <el-input v-model="smsLoginForm.code" placeholder="请输入验证码" class="input-item">
                <template #prefix>
                  <img src="/image/icon/code.svg" alt="code icon" class="input-icon"/>
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
            <el-button type="primary" @click="submitSmsForm" class="login-button">登录</el-button>
          </el-form-item>
          <div class="msg">
            没有账号? <router-link to="/register">点击注册</router-link>
          </div>
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
      isAccountLogin: true, // 默认为账号登录
      agreedToTerms: false,
      loginForm: {
        account: '',
        password: '',
        captcha: '',
        agreedToTerms: false
      },
      loginRules: {
        account: [{ required: true, message: '请输入用户名/邮箱/密码', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      smsLoginForm: {
        account: '',
        code: '',
      },
      smsLoginRules: {
        account: [{ required: true, message: '请输入邮箱或手机号', trigger: 'blur' }],
        smsCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      captchaImage: 'http://localhost:8080/auth/captcha',
      countdown:0,
      isSending:false,
      captchaInterval:null,
    };
  },
  methods: {
    sendEmailCode() {
      if(this.smsLoginForm.account === ''){
        this.$message.error('请输入手机号或邮箱');
        return;
      }
      const phoneRegex = /^1[3-9]\d{9}$/;
      const emailRegex = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
      if (phoneRegex.test(this.smsLoginForm.account)) {
        // 输入的是手机号，调用发送短信的API
        axios.post("http://localhost:8080/auth/send-phone-code", {
          to: this.smsLoginForm.account,
          subject: '您的登录验证码已发送，请在5分钟内使用,如非本人操作，请忽略本邮件。',
        })
            .then(response => {
              console.log(response);
              this.startCountdown();
            }).catch(error => {
          console.error(error);
        });
      } else if (emailRegex.test(this.smsLoginForm.account)) {
        // 输入的是邮箱，调用发送邮件的API
        axios.post("http://localhost:8080/auth/send-email",
            {
              to: this.smsLoginForm.account,
              subject: '您的登录验证码已发送，请在5分钟内使用,如非本人操作，请忽略本邮件。',
            })
            .then(response => {
              console.log(response);
              this.startCountdown();
            }).catch(error => {
          console.error(error);
        });
      } else {
        this.$message.error('请输入正确的手机号或邮箱');
      }
    },

    submitForm() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          let account= {
            account: this.loginForm.account,
            password: this.loginForm.password,
            captcha: this.loginForm.captcha
          }
          if(!this.agreedToTerms){
            this.$message.error('请阅读并接受用户协议和隐私政策');
            return;
          }
          axios.post('http://localhost:8080/auth/login', account
          ).then(response => {
            if (response.data.statusCode === 200) {
              document.cookie = 'token=' + response.data.data.token;
              document.cookie = 'username=' +response.data.data.username;
              this.$message.success(response.data.message);
              this.$router.push('/index');
            } else {
              this.$message.error(response.data.message);
            }
          }).catch(error => {
                console.error(error);
          });
        }
      });
    },

    submitSmsForm() {
      console.log('submitSmsForm is called');

      const phoneRegex = /^1[3-9]\d{9}$/;
      const emailRegex = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
      let postUrl='';

      if(phoneRegex.test(this.smsLoginForm.account)) {
        postUrl = 'http://localhost:8080/auth/login-by-phone-code';
      }else if (emailRegex.test(this.smsLoginForm.account)) {
        postUrl = 'http://localhost:8080/auth/login-by-email-code';
      }else{
        this.$message.error('请输入正确的手机号或邮箱');
        return;
      }

      axios.post(postUrl,
          { account: this.smsLoginForm.account,
                  code: this.smsLoginForm.code})
          .then(response => {
        if (response.data.statusCode === 200){
          document.cookie='token='+response.data.data.token;
          document.cookie='username='+response.data.data.username;
          this.$router.push('/index');
        }
      }).catch(error => {
        console.error(error);
      });
    },
    refreshCaptcha() {
      axios.get('http://localhost:8080/auth/captcha', { responseType: 'arraybuffer' })
          .then(response => {
            let base64Captcha = btoa(
                new Uint8Array(response.data)
                    .reduce((data, byte) => data + String.fromCharCode(byte), '')
            );
            this.captchaImage = 'data:image/jpeg;base64,' + base64Captcha;
          })
          .catch(error => {
            console.error(error);
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
  },
  mounted() {
    this.captchaInterval = setInterval(this.refreshCaptcha, 60000);
  },

  beforeDestroy() {
    clearInterval(this.captchaInterval);
  },
};


</script>

<style scoped>
.no-account-and-to-register{
  padding-left:20px;
}
.disabled-button {
  background-color: #ccc !important;
  border-color: #ccc !important;
  cursor: not-allowed !important;
}

.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('public/image/common/authBackground.jpg');
  background-size: cover;
}

.login-wrapper {
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
  color:linen;
}

.input-item {
  margin-bottom: 10px;
  height: 55px;
}

.captcha {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.captcha-image {
  width: 200px;
  cursor: pointer;
}

.msg {
  font-size: 20px;
  margin-top: 20px;
  padding-left:20px;
}

.register-link {
  color: #abc1ee;
  cursor: pointer;
  text-decoration: none;
}

.login-wrapper-form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-left: 10px;
  margin-top:-30px;
}

.login-wrapper-aside{
  display: flex;
  justify-content: center; /* 设置justify-content为center */
  align-items: center; /* 设置align-items为center */
  height: 100%;
  margin: 20px 30px 10px 20px;
}

.aside{
  width: 50%; /* 设置宽度为80% */
  height: 100%;
  object-fit: cover;
  border-bottom-left-radius: 30px;
  font-size: 20px;
}

.aside img {
  margin-top:0;
  width: 80%;
  height: 60%; /* 设置高度为100% */
  object-fit: cover; /* 图片保持宽高比，同时填充元素的整个内容框 */
  border-radius: 30px;
  align-items: center;
  padding: 30px;
  background: transparent;
}

.account-input{
  width:100%;
  height: auto;
  border-radius: 30px;
}

.aside-info{
  font-size: 24px;
  font-weight: bold;
  text-align: left;
  margin-bottom: 20px;
  background-color: rgba(240, 240, 240, 0);
  padding: 20px;
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
}

.login-option {
  cursor: pointer;
  color: gray;
  font-size: 24px;
  margin: 20px 30px;
}

.login-option.selected {
  font-weight: bold;
  color: black;
  border-bottom: 2px solid blue;
}

.login-option-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.login-button{
  width: 100%;
  height: 55px;
  border-radius: 10px;
  margin-top: 40px;
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

.input-icon{
  width: 30px;
  height:30px;
}

</style>
