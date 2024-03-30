<template>
  <md-editor v-model="text"
             class="editor"
             @on-upload-img="handleUploadImage"
  />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import axios from "axios";
import {ref} from 'vue'

export default defineComponent({
  components: { MdEditor },
  setup() {
    let text = ref('');
    const handleUploadImage = async (files) => {
      console.log('upload image:', files[0])
      let file = files[0];
      const formData = new FormData();
      formData.append('file', file);

      const response = await axios.post('http://localhost:8080/blog/upload-image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        console.log('upload image success:', response.data.data)
        const imageUrl = response.data.data;
        const imageMarkdown = `![](${imageUrl})\n`;
        text.value += imageMarkdown;
      }).catch(error => {
        console.error('upload image failed:', error)
      })
    };
    return {
      text,
      handleUploadImage
    }
  }
});
</script>