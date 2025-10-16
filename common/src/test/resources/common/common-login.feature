@ignore
Feature: 用户登录

Background:
  * configure driver = { type: 'chrome', showDriverLog: true }
  * driver 'http://10.1.19.59:30002/#/login'

Scenario: 登录流程

* waitFor('.el-input__inner')

* waitFor('.login-button button')
* waitFor('.el-input__inner')
* input('form .el-form-item:first-child .el-input__inner', 'Testdemo888')
* input('form .el-form-item:last-child .el-input__inner', 'asb#1234')

* click('.login-button button')
* waitFor('#app > div.box-100 > div.box-100.layout-container > div.layout-left-container > div > div > div.logo-container > div')

* delay(5000)



