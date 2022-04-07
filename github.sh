cmt=$1
if [ -z $1 ]; then
  cmt=fixed
fi


git --git-dir=.github add .
git --git-dir=.github commit -m '$cmt'
git --git-dir=.github push