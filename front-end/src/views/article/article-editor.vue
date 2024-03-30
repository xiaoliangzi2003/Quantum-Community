<template>
  <el-container direction="vertical" class="manage-container">
    <Navigation/>
    <el-container>
      <el-main class="editor-main">
        <el-card class="box-card">
          <el-card class="article-brief">
            <el-form :model="form" class="label-form" label-width="100px">
              <el-form-item label="文章标题">
                <el-input v-model="form.title" class="input-item"></el-input>
              </el-form-item>
              <el-form-item label="作者名">
                <el-input v-model="form.author" :disabled="false" class="input-item"></el-input>
              </el-form-item>
              <el-form-item label="分类">
                <el-select v-model="form.category" placeholder="请选择" class="input-item">
                  <el-option
                      v-for="item in categories"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="标签" class="input-item">
                <el-tag
                    v-for="(tag, index) in form.tags"
                    :key="index"
                    closable
                    @close="handleClose(index)"
                >
                  {{ tag }}
                </el-tag>
                <el-input
                    v-model = "form.input"
                    @keyup.enter.native="handleInputConfirm"
                    placeholder="请输入标签，按Enter确认"
                ></el-input>
              </el-form-item>
              <el-form-item label="文章简介" class="input-item">
                <el-input
                    type="textarea"
                    :rows="2"
                    placeholder="请输入文章简介，100字以内"
                    v-model="form.description"
                    :show-word-limit="true"
                    :maxlength="100"
                ></el-input>
              </el-form-item>
            </el-form>
          </el-card>
          <el-card class="editor-card">
            <article-editor theme="light" ref="articleEditor" class="article-editor"/>
          </el-card>
          <el-card class="publish-setting">
            <el-form-item label="是否公开">
              <el-switch v-model="form.isPublic"></el-switch>
            </el-form-item>
            <el-form-item label="是否定时发布">
              <el-switch v-model="form.isScheduled"></el-switch>
              <el-date-picker
                  v-if="form.isScheduled"
                  v-model="form.scheduleTime"
                  type="datetime"
                  class="day-picker"
                  placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <div class="bottom-clearfix">
              <el-button type="primary" @click="publish">发布</el-button>
            </div>
          </el-card>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import ArticleEditor from '../../components/article/editor.vue'
import axios from 'axios';
import Cookies from 'js-cookie';
import Navigation from '../../components/index/header.vue'
export default {
  data() {
    return {
      form: {
        title: '',
        author: Cookies.get('username') || '',
        category: '',
        tags: [],
        input:'',
        isPublic: true,
        isScheduled: false,
        scheduleTime: '',
        description: '',
      },
      categories: ['知识' , '科学', '脑洞', '游戏', '财经', '心理', '故事', '职场', '旅行', '法律', '国际', '美食', '动画', '宠物', '时尚', '时事', '健康', '体育']
    };
  },
  components: {
    ArticleEditor,
    Navigation
  },
  created(){
    this.form.author = Cookies.get('username') || '';
  },
  methods:{
    handleInputConfirm() {
      if (this.form.input.trim() !== '') {
        // 添加标签到数组
        this.form.tags.push(this.form.input.trim());
        // 清空输入框
        this.form.input='';
      }
    },
    handleClose(index) {
      this.form.tags.splice(index, 1);
    },
    publish(){
      if (this.form.isScheduled) {
        this.form.scheduleTime = new Date(this.form.scheduleTime).toLocaleString();
      }

      const paragraphs = this.$refs.articleEditor.text.split('\n');
      for (const paragraph of paragraphs) {
        if (!paragraph.startsWith('#') && // 排除标题
            !paragraph.startsWith('---') && // 排除分隔线
            !paragraph.includes('<!--') && // 排除注释
            !paragraph.startsWith('```') && // 排除代码段
            !paragraph.includes('![') && // 排除图片
            !(paragraph.includes('|') && paragraph.includes('-')) && // 排除表格
            !paragraph.startsWith('$$')) { // 排除数学公式
          this.form.description += paragraph + ' ';
          if (this.form.description.length >= 100) {
            // 如果提取的正文部分超过100个字符，截取前100个字符
            this.form.description = this.form.description.slice(0, 100);
            break;
          }
        }
      }
      if (!this.form.title || !this.form.author || !this.$refs.articleEditor.text) {
        this.$message.error('标题、作者和内容不能为空');
        return;
      }

      if (this.form.title.length > 30) {
        this.$message.error('标题长度不能超过30个字符');
        return;
      }

      let article={
        author: this.form.author,
        title: this.form.title,
        category: this.form.category,
        tags: this.form.tags.join(','),
        isPublic: this.form.isPublic,
        isScheduled: this.form.isScheduled,
        scheduleTime: this.form.scheduleTime,
        content:this.$refs.articleEditor.text,
        summary:this.form.description,
      };
      axios.post('http://localhost:8080/blog/publish-article',article)
          .then(response=>{
            if(response.data.statusCode===200){
              console.log('发布成功');
              this.$router.push('/article-success');
            }
          }).catch(error=>{
            console.log(error)
      });
    }
  }
}
</script>

<style scoped>
.input-item{
  width: 100%;
  text-align: right;
}
.day-picker{
  padding-left:5px;
}

.article-brief{
  margin-bottom: 20px;
}

.publish-setting{
  margin-top: 20px;
}

.editor-card{
  height: 100%;
}

.publish-link {
  color: #3498db; /* 设置文字颜色 */
  text-decoration: none; /* 去掉下划线 */
  font-size: 16px; /* 设置字体大小 */
  cursor: pointer; /* 当鼠标悬停时显示手形光标 */
}

.editor-aside-menu{
  background-color: transparent;
}
.editor-aside {
  background-color: #2c3e50; /* 更改为深灰色 */
  color: white; /* 更改为白色 */
  margin-top:0;
  padding-top:0;
}

.manage-container {
  height: 100vh;
  margin-bottom: 0;
  padding-bottom: 0;
}

.index-top-navigation {
  background-color: black;
  margin-bottom: 0;
  padding-bottom: 0;
  height: 70px;
}

.search-input {
  color: white;
  background-color: black;
  font-size: 16px;
}

.horizontal-navigation{
  background-color: black;
  margin-bottom: 0;
  color: white;
  border-bottom: none;
}

.navigation-item{
  color: white;
  height: auto;
}

.article-editor{
  margin: 0;
  padding-bottom: 0;
  padding-left: 0;
  background-color: #ecf0f1; /* 更改为浅灰色 */
  color: #2c3e50; /* 更改为深蓝色 */
  height: 80vh;
}

.editor-main{
  padding:10px;
  overflow: hidden;
}

.bottom-clearfix {
  text-align: right;
  margin-top: 20px;
}

.box-card{
  height: 100%;
  padding: 20px;
  margin: 0;
  box-shadow:0px 0px 10px 10px rgba(0,0,0,0.75);
  background-color: #ecf0f1; /* 更改为浅灰色 */
  color: #2c3e50; /* 更改为深蓝色 */

}
</style>