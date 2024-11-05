Feature: ui test

Scenario Outline: <type>
  * configure driver = { type: '#(type)', showDriverLog: true }

  * driver 'https://google.com'
  * click
  * match text('#placeholder') == 'Before'
  * click('{}Click Me')
  * match text('#placeholder') == 'After'

Examples:
| type         |
| chrome       |
#| chromedriver |
#| geckodriver  |
#| safaridriver |