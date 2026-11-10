import re
p=r'C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\lib\armeabi-v7a\liblwplocal.so'
data=open(p,'rb').read()
strs=re.findall(rb'[\x20-\x7e]{4,}', data)
out=[]
seen=set()
for s in strs:
    t=s.decode('ascii',errors='ignore')
    if t not in seen:
        seen.add(t)
        out.append(t)
with open(r'C:\Users\alfon\TGZ\wave\so_strings.txt','w',encoding='utf-8') as f:
    for t in out:
        f.write(t+'\n')
print('unique strings:', len(out))