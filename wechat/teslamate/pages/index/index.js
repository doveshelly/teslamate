const app = getApp();
Page({
  data: {
    isLoggedIn: '0',
    // 最终版数据结构，包含行程、充电和停车
    allReportData: {
      drives: {},
      charges: {},
      parks: {},
      summary:{}
    },
    currentDate: '',
    formattedDate: '',
    tabs: [{
        id: 'summary',
        name: '月报'
      }, {
        id: 'drives',
        name: '行程'
      },
      {
        id: 'charges',
        name: '充电'
      }, {
        id: 'parks',
        name: '停车'
      },
    ],
    currentTab: 'drives', // 默认选中“行程”
    currentTabName: '行程', // 新增：当前选中Tab的名称, 并设置初始值
    displayList: [], 
    monthData: []
  },

  /**
   * 使用 onShow 是因为：
   * 1. 每次进入页面都会触发，无论是首次进入还是从下一页返回。
   * 2. 当从绑定手机页返回时，可以重新执行登录检查，拉取最新信息。
   */
  onShow: function () {
    // 检查本地是否有 Token，决定显示哪个视图
    const tokenName = wx.getStorageSync('tokenName');
    const tokenValue = wx.getStorageSync('tokenValue');
    //  设置默认日期为当前月份
    this.setDefaultDate();
    if (tokenName && tokenValue) {
      console.log("Token 存在, 根据token获取用户信息...");
      this.setData({
        isLoggedIn: '1'
      });
      this.fetchData(this.data.currentTab, this.data.currentDate);
    } else {
      console.log("token不存在,调用code获取用户信息接口.");
      // Token不存在，执行完整的登录和数据获取流程
      this.checkLoginAndFetchData();
    }
  },

  /**
   * 新增辅助方法：设置默认日期
   */
  setDefaultDate: function () {
    const now = new Date();
    const year = now.getFullYear();
    // getMonth() 返回的是 0-11，所以需要+1。并补零以满足 'YYYY-MM' 格式。
    const month = String(now.getMonth() + 1).padStart(2, '0');

    this.setData({
      currentDate: `${year}-${month}`, // e.g., "2025-07"
      formattedDate: `${year}年${month}月` // e.g., "2025年07月"
    });
  },

  formatDateForDisplay(dateString) {
    if (!dateString) return '';
    const parts = dateString.split('-');
    return `${parts[0]}年${parts[1]}月`;
  },

  onDateChange: function (e) {
    const newDate = e.detail.value;
    if (this.data.currentDate !== newDate) {
      this.setData({
        currentDate: newDate,
        formattedDate: this.formatDateForDisplay(newDate)
      });
      this.fetchData(this.data.currentTab, newDate);
    }
  },

  /**
   * Tab切换事件
   */
  switchTab: function (e) {
    const newTabId = e.currentTarget.dataset.id;
    if (this.data.currentTab !== newTabId) {
      // 从tabs数组中找到新tab的完整对象，以获取name
      const newTab = this.data.tabs.find(item => item.id === newTabId);

      this.setData({
        currentTab: newTabId,
        currentTabName: newTab ? newTab.name : '' // 更新Tab名称
      });
      this.fetchData(newTabId, this.data.currentDate);
    }
  },

  fetchData: function (tab, date) {
    wx.showLoading({
      title: '正在加载...'
    });
    if (this.data.allReportData[tab]) {
      let url = '';
      if (tab === 'summary') {
        url = '/api/summary';
      } else {
        url = '/api/' + tab + '/monthly';
      }

      app.request({
        url: url,
        method: 'POST',
        data: {
          month: date
        }
      }).then(res => {
        wx.hideLoading();
        if (res.data && res.data.rtCode === '0000' && res.data.rtData) {
          const data = res.data.rtData;
          if (tab === 'summary') {
            this.setData({monthData: data});
          } else {
            this.setData({displayList: data});
          }
        } else {
          wx.showToast({
            title: res.data.rtMessage,
            icon: 'none'
          });
        }
      }).catch(err => {
        wx.hideLoading();
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        console.error(err);
      });
    }

  },

  /**
   * 核心函数：检查登录状态并获取所有业务数据
   */
  checkLoginAndFetchData: function () {
    wx.showLoading({
      title: '正在登录...'
    });

    // 1. 调用 app.js 的全局登录函数获取 code
    app.login(code => {
      if (!code) {
        wx.hideLoading();
        wx.showToast({
          title: '登录失败，请重试',
          icon: 'none'
        });
        return;
      }

      // 2. 拿着 code 请求自己的后台，检查用户是否已绑定
      app.request({
        url: '/user/login',
        method: 'POST',
        data: {
          code: code
        }
      }).then(res => {
        wx.hideLoading();
        if (res.data && res.data.rtCode === '0000' && res.data.rtData) {
          const tokenInfo = res.data.rtData.tokenInfo;
          wx.setStorageSync('tokenName', tokenInfo.tokenName);
          wx.setStorageSync('tokenValue', tokenInfo.tokenValue);
          this.setData({
            isLoggedIn: '1'
          });
          // 获取到用户信息后，再去查询记录
          this.fetchData(this.data.currentTab, this.data.currentDate);
        } else {
          this.setData({
            isLoggedIn: '2'
          });
          // 获取openId失败，显示后端返回的错误信息
          wx.showToast({
            title: res.data.rtMessage,
            icon: 'none'
          });
        }
      }).catch(err => {
        wx.hideLoading();
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        console.error(err);
      });
    });
  }
});