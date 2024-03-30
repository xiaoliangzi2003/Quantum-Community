<template>
  <el-form ref="form" :model="form" label-width="120px">
    <el-form-item label="头像">
      <vue-cropper ref="cropper" :src="form.avatar" @crop="onCrop"></vue-cropper>
      <input type="file" @change="previewAvatar" accept="image/*">
      <el-button @click="uploadAvatar">上传头像</el-button>
    </el-form-item>
    <div class="name-sex-edit">
      <el-form-item label="昵称">
        <el-input v-model="form.nickname"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender">
          <el-option label="男" value="male"></el-option>
          <el-option label="女" value="female"></el-option>
        </el-select>
      </el-form-item>
    </div>

    <el-form-item label="生日">
      <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期"></el-date-picker>
    </el-form-item>
    <el-form-item label="个性签名">
      <el-input v-model="form.signature"></el-input>
    </el-form-item>
    <el-form-item label="邮箱">
      <el-input v-model="form.email"></el-input>
    </el-form-item>
    <el-form-item label="手机号">
      <el-input v-model="form.phone"></el-input>
    </el-form-item>
    <el-form-item label="微信号">
      <el-input v-model="form.wechat"></el-input>
    </el-form-item>
    <el-form-item label="Github账号">
      <el-input v-model="form.github"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElDatePicker, ElButton } from 'element-plus';
import VueCropper from 'vue-cropper';
import axios from "axios";
import Cookies from "js-cookie";

export default {
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElDatePicker,
    ElButton,
    VueCropper
  },
  data() {
    return {
      form: {
        avatar: '',
        nickname: '',
        gender: '',
        birthday: '',
        signature: '',
        email: '',
        phone: '',
        wechat: '',
        github: ''
      }
    }
  },
  methods: {
    onCrop(dataUrl) {
      this.form.avatar = dataUrl;
    },
    uploadAvatar() {
      // 获取用户头像的文件
      const avatarFile = this.$refs.cropper.getCropData();

      // 从cookie中获取username
      const username = Cookies.get('username');

      // 创建一个新的FormData对象
      let formData = new FormData();

      //如果后缀不是jpg或png或jpeg，提示用户重新上传
      if (!avatarFile.type.includes('image')) {
        this.$message.error('请上传图片文件');
        return;
      }

      // 添加文件到formData对象，文件名为'username_avatar'
      formData.append('file', avatarFile, `${username}_avatar`);

      // 使用axios发送POST请求
      axios.post('http://localhost:8080/user-profile/upload-avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        if (response.data.status === 'OK') {
          this.$message.success('上传头像成功');
        } else {
          this.$message.error('上传头像失败');
        }
      })
          .catch(error => {
            console.error(error);
            this.$message.error('上传头像失败');
          });
    },
    onSubmit() {

    },
    previewAvatar(event) {
      // 获取用户选择的文件
      const file = event.target.files[0];

      // 创建一个新的FileReader对象
      const reader = new FileReader();

      // 当文件读取完成后，将结果设置为头像的预览
      reader.onloadend = () => {
        this.form.avatar = reader.result;
      };

      // 开始读取文件
      if (file) {
        reader.readAsDataURL(file);
      } else {
        this.form.avatar = '';
      }
    },
  }
};
</script>

<style scoped>
.name-sex-edit{
  flex-direction: row;
  display: flex;
  justify-content: space-between;
}
.el-form {
  width: 500px;
  margin: 0 auto;
}
.vue-cropper {
  width: 100px;
  height: 100px;
}
</style>