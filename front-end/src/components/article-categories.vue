<template>
  <div class="article-categories">
    <h3>文章分类</h3>
    <ul>
      <li><a href="#">Java</a></li>
      <li><a href="#">C/C++</a></li>
      <li><a href="#">Python</a></li>
      <li><a href="#">数据结构与算法</a></li>
      <li><a href="#">计算机网络</a></li>
      <li><a href="#">操作系统</a></li>
      <li><a href="#">计算机组成原理</a></li>
      <li><a href="#">数据库</a></li>
    </ul>

  </div>
</template>

<script>
export default {
  name: 'ArticleCategories'
}
</script>

<style scoped>
body {
  font-family: 'Roboto', sans-serif;
  color: #333;
  line-height: 1.6;
  background-color: white;
  background-size: cover;
  margin:0;
  padding:0;
  overflow-x: hidden;
  max-width: 100%;
}

code{
  display: inline; /* 让代码块与其他文本内容在同一行 */
  background-color: #f4f4f4; /* 添加灰色背景 */
  padding: 2px 4px; /* 添加一些内边距，使代码块内容与背景有一定间距 */
  border-radius: 4px; /* 添加一些圆角 */
}

pre code {
  background-color: black  !important;;
  color: #ffffff;
  padding: 10px; /* 添加一些内边距 */
  display: block; /* 让代码段占据整个行 */
  overflow-x: auto; /* 如果代码段太长，添加滚动条 */
  border:none;
  border-radius: 5px;
  font-family: 'Courier New', monospace;
}


.navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.9);
  box-shadow: 0 0 30px rgba(0,0,0,1);
  width: 100%;
  padding: 20px 50px;
  margin:0;
}
nav ul {
  display: flex;
  list-style: none;
}
nav ul li {
  margin-right: 40px;
}
nav ul li:last-child {
  margin-right: 0;
}
nav ul li a {
  color: white;
  text-decoration: none;
  font-size:15px;
}
nav ul li a:hover {
  color: #efecec;
  transform:scale(1.1);
}
nav input[type="search"] {
  padding: 10px;
  margin-right: 100px;
  border: 1px solid #ddd;
  border-radius: 10px;
  align-items: center;
}

.article-info h1 {
  text-align: center;
  flex-grow: 2; /* 让标题占据更多的空间，以保证居中 */
}

.article-info p {
  flex-grow: 1; /* 让作者和日期占据较少的空间，以保证它们位于两侧 */
}

img {
  background: none;
}


.article-info h1 {
  text-align: center;
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  text-align: center;
}

.article-categories, .other-content {
  width: 30%; /* 每个占据 30% 的宽度 */
  flex-direction: column;
}

.article-categories{
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: rgba(255, 255, 255, 0.8);
  font-size: 24px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.article-categories ul {
  list-style-type: none;
}

.article-categories li {
  padding: 10px;
  margin-bottom: 5px;
}

.article-categories a{
  text-decoration: none; /* 去除下划线 */
  color: black; /* 设置颜色为黑色 */
  font-weight: bold; /* 加粗 */
}
</style>