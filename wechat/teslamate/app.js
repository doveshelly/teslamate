// app.js
const crypto = require('./utils/crypto-js.min');
// 将后端 API 的基础路径定义为常量，方便统一管理和修改，小程序接口必须使用域名
const API_BASE_URL = 'https://xxxx.xx';
// const API_BASE_URL = 'http://127.0.0.1:8000';
App({
  onLaunch() {
    // 小程序启动时，不立即执行登录。
    // 登录操作由需要登录态的页面主动触发。
  },

  globalData: {
    userInfo: null, // 用于存储从我们自己服务器获取的用户信息
    // 后端定义的签名密钥，必须和后端保持一致！
    signSecret: '1234qwerasdfzxcv1234awerasdfzxc',
    apiUrl: API_BASE_URL,
  },

  /**
   * 封装全局的登录函数，供页面调用
   * @param {function} callback - 获取到 code 后的回调函数，code会作为参数传入
   */
  login: function (callback) {
    wx.login({
      success: res => {
        if (res.code) {
          // 成功获取 code，通过回调函数返回
          console.log('获取登录凭证(code)成功:', res.code);
          callback(res.code);
        } else {
          // 登录失败
          console.error('wx.login 失败! ' + res.errMsg);
          wx.showToast({
            title: '登录失败，请检查网络',
            icon: 'none'
          });
          callback(null);
        }
      },
      fail: err => {
        console.error('wx.login 调用失败:', err);
        wx.showToast({
          title: '无法登录，请稍后重试',
          icon: 'none'
        });
        callback(null);
      }
    });
  },

  /**
   * 封装的统一请求函数
   * @param {object} options - wx.request 的参数对象
   * @returns {Promise}
   */
  request: function (options) {
    // 如果传入的 options.url 已经是完整的 http/https 路径，则不进行拼接
    if (options.url && !options.url.startsWith('http')) {
      options.url = this.globalData.apiUrl + options.url;
    }
    return new Promise((resolve, reject) => {
      const tokenValue = wx.getStorageSync('tokenValue');
      const tokenName = wx.getStorageSync('tokenName');

      // 如果没有 token 但请求又需要授权，则直接拒绝
      if (options.auth && (!tokenValue || !tokenName)) {
        console.error('Request requires token, but token is not found.');
        // 清理状态并跳转到登录页
        wx.clearStorageSync();
        wx.reLaunch({
          url: '/pages/index/index',
        });
        reject({
          errCode: -1,
          errMsg: 'No token found'
        });
        return;
      }

      // --- 签名生成 ---
      const nonce = Math.random().toString(36).substring(2, 15);
      const timestamp = Date.now().toString();
      let paramsString = '';

      if (options.data) {
        const sortedKeys = Object.keys(options.data).sort();
        paramsString = sortedKeys.map(key => `${key}=${options.data[key]}`).join('&');
        options.data = paramsString; // 关键：data 变成字符串
      }

      // --- 设置请求头 ---
      const header = options.header || {};
      header['X-Nonce'] = nonce;
      header['X-Timestamp'] = timestamp;
      header['X-Signature'] = crypto.SHA256(paramsString + nonce + timestamp + this.globalData.signSecret).toString();
      header['Content-Type'] = 'application/x-www-form-urlencoded'; // 告诉服务端这是表单格式

      if (tokenValue && tokenName) {
        header[tokenName] = tokenValue;
      }

      // --- 发起请求 ---
      wx.request({
        ...options,
        header: header,
        success: (res) => {
          console.log('请求返回:' + JSON.stringify(res))
          // 在这里可以做统一的响应处理
          // 例如，如果后端返回 token 失效 (如401)，则清除 token 并重新登录
          if (res.data && res.data.rtCode === '9992') {
            console.log('token过期或不存在跳转到主页重新获取.');
            wx.removeStorageSync('tokenName');
            wx.removeStorageSync('tokenValue');
            wx.reLaunch({
              url: '/pages/index/index' // 或者你的登录引导页
            });
            reject(res);
          } else {
            resolve(res);
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  }
})