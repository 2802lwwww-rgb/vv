# 阶段一测试指南

## 📋 测试前准备

1. **确保目录存在**
   ```powershell
   # 创建文件上传目录
   mkdir D:\upload\red-education
   ```

2. **准备测试文件**
   - 一张图片（JPG/PNG，用于测试头像/封面上传）
   - 一个小视频（MP4，用于测试课程视频上传，可选）

3. **导入 Postman 集合**
   - 文件：`RedEducation-Stage1-Tests.json`
   - 在 Postman 中点击 Import 导入

## 🧪 测试步骤

### 测试 1：文件上传功能

1. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

2. **使用 Postman 测试**
   - 运行 `1. Login` 获取 Token（自动保存到环境变量）
   - 运行 `2. Upload Image File`
     - 在 Body → form-data 中选择一张图片
     - 点击 Send
     - 预期返回：
       ```json
       {
         "code": 200,
         "message": "操作成功",
         "data": "/files/2025/11/28/xxxxxxxx.jpg"
       }
       ```
   - 运行 `3. Upload Video File`（可选）

3. **验证文件**
   - 检查目录 `D:\upload\red-education\2025\11\28\` 是否有文件
   - 访问 URL 测试（需要先访问任意接口触发应用启动）

---

### 测试 2：积分系统

1. **查看初始积分**
   - 运行 `4. Get User Info (Check Initial Points)`
   - 查看返回的 `points` 字段

2. **完成课程获得积分**
   - 运行 `5. Complete Course (Earn Points)`
   - 此时系统会：
     - 创建学习记录（如果是第一次）
     - 标记课程完成
     - **自动发放积分**（10分）

3. **验证积分增加**
   - 运行 `6. Get User Info Again (Verify Points Increased)`
   - Postman 自动验证积分是否增加

4. **数据库验证**
   - 打开 MySQL 客户端
   - 执行 `database/test_stage1_points.sql` 中的查询
   - 查看 `point_record` 表是否有新记录

---

## 📊 预期结果

### 文件上传
✅ 返回的 URL 格式：`/files/YYYY/MM/DD/uuid.ext`  
✅ 文件实际保存在本地目录  
✅ 文件大小和格式验证有效

### 积分系统
✅ `point_record` 表插入新记录  
✅ `user.points` 字段增加  
✅ 日志输出积分变动信息

---

## 🐛 常见问题

### 1. 文件上传失败
- **检查目录权限**：确保 `D:\upload\red-education` 可写
- **检查配置**：`application.yml` 中的 `file.upload-path` 是否正确

### 2. 积分未增加
- **检查课程是否首次完成**：重复完成同一课程不会重复发放积分
- **检查日志**：查看是否有 `用户 X 积分变动` 的日志
- **检查数据库**：使用 SQL 查询 `point_record` 表

### 3. Token 无效
- **重新登录**：运行 `1. Login` 请求重新获取 Token

---

## 📝 SQL 验证脚本

```sql
-- 查看用户积分
SELECT id, username, points FROM user WHERE username = 'testuser';

-- 查看积分记录
SELECT * FROM point_record WHERE user_id = 3 ORDER BY create_time DESC;

-- 查看学习记录
SELECT * FROM study_record WHERE user_id = 3 AND course_id = 1;

-- 验证积分一致性
SELECT 
    u.points AS user_points,
    COALESCE(SUM(pr.points), 0) AS total_earned
FROM user u
LEFT JOIN point_record pr ON u.id = pr.user_id
WHERE u.id = 3;
```

---

## ✅ 测试通过标准

- [ ] 图片上传成功并返回正确 URL
- [ ] 文件实际保存到本地目录
- [ ] 完成课程后积分自动增加（+10）
- [ ] `point_record` 表有对应记录
- [ ] `user.points` 与 `point_record` 总和一致
