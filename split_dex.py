import re
src=open(r'C:\Users\alfon\TGZ\wave\dexdump.txt',encoding='utf-8',errors='ignore').read()
# split by "Class #"
parts=re.split(r'(?=Class #\d+)', src)
# map class descriptor -> text
classes={}
for part in parts:
    m=re.search(r"Class descriptor\s*:\s*'([^']+)'", part)
    if m:
        classes[m.group(1)]=part
print('num classes', len(classes))
# write index
with open(r'C:\Users\alfon\TGZ\wave\dex_classes.txt','w',encoding='utf-8') as f:
    for k in sorted(classes):
        f.write(k+'\n')
# save each class to file
import os
outdir=r'C:\Users\alfon\TGZ\wave\dex_classes'
os.makedirs(outdir,exist_ok=True)
for k,v in classes.items():
    name=k.strip('L;').replace('/','_').replace('$','_')
    with open(os.path.join(outdir,name+'.txt'),'w',encoding='utf-8') as f:
        f.write(v)
print('done')
PY