# VuaTroVN app powered by NTG Internship 16/12/2025 Batch
http://localhost:8080/api/auth
POST /signup
{
  "username": "kiet123",
  "email": "kiet@gmail.com",
  "password": "123456"
}
{
  "id": 1,
  "username": "kiet123",
  "email": "kiet@gmail.com",
  "status": "PENDING"
}
GET /verify?token=xxxxx
Email verified successfully
POST /forgot-password
{
  "email": "kiet@gmail.com"
}
Reset link sent successfully
