data=open(r'C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\res\raw\defaultcontainer_binary','rb').read()
print('len',len(data))
import re
print(re.findall(rb'[\x20-\x7e]{4,}', data)[:40])