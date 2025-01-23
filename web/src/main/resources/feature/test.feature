Feature: ui test

Scenario Outline: <type>
  * def recordCDNSetup =
  """
  const createScript = document.createElement("script")
  createScript.setAttribute("src", "https://cdn.jsdelivr.net/npm/@rrweb/record@latest/dist/record.umd.min.cjs")
  const doc = document.documentElement;
  doc.insertBefore(createScript, doc.firstChild)
  """
  * configure driver = { type: '#(type)', showDriverLog: true }
  * call recordCDNSetup
  * driver 'http://www.google.com/'
  * submit().input('#gLFyf', "iframe")

  Examples:
    | type         |
    | chrome       |
#| chromedriver |
#| geckodriver  |
#| safaridriver |