// 登录页面相关脚本

// 表单验证
function validateForm() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        alert('请输入用户名和密码');
        return false;
    }
    
    return true;
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            if (!validateForm()) {
                event.preventDefault();
            }
        });
    }
    
    // 为输入框添加键盘事件
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                loginForm.dispatchEvent(new Event('submit'));
            }
        });
    }
});