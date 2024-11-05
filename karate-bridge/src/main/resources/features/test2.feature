Feature: ui test  2

Scenario Outline: <type>
  * configure driver = { type: '#(type)', showDriverLog: true }

  * driver 'https://google.com'
  * click
  * match text('#placeholder') == 'Before'


Examples:
| type         |
| chrome       |
#| chromedriver |
#| geckodriver  |
#| safaridriver |