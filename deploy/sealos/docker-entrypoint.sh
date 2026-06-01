#!/bin/sh
# =============================================
#  ????????
#  ? __BACKEND_HOST__ ?????????
# =============================================
set -e

BACKEND_HOST="${BACKEND_HOST:-myblog-backend}"

echo "[myblog-frontend] Backend host: $BACKEND_HOST"
sed -i "s/__BACKEND_HOST__/$BACKEND_HOST/g" /etc/nginx/conf.d/default.conf

exec nginx -g "daemon off;"
