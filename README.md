# Android V3 签名
我们知道Android app的签名是不能更改的，否则就无法升级，但是有时候我们有更新app签名的需求，Google在9.0新增了V3签名方案
就是为了解决签名替换的。

目前的android studio和gradle都还不支持v3签名的配置，所以如果想体验V3签名暂时只能使用apksigner手动签名。<br/>
(android gradle build tools 4.2.0-alpha版本支持了 **enableV3Signing** 参数，不过暂时还没看到详细的用法)。

本Demo演示了如何使用 [**apksigner**](https://developer.android.com/studio/command-line/apksigner) 手动进行app的签名替换。

## 准备keystore文件

首先准备2个keystore文件，一个用户正常版本，一个用户新版本，我们测试新版本替换新的keystore后是否能正常覆盖安装应用，这里生成的两个签名
文件alias分别为old和new，密码都是123456。

## 使用 old.jks 签名打包应用，查看签名
```
./gradlew aR
apksigner verify -v --print-certs app/build/outputs/apk/release/app-release.apk
-------------------
Verifies
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): false
Verified using v4 scheme (APK Signature Scheme v4): false
Verified for SourceStamp: false
Number of signers: 1
Signer #1 certificate DN: CN=Old, OU=Old, O=Old
Signer #1 certificate SHA-256 digest: 22267033e1741f6b7c437eba9cd65164f1703a561ce685dfe0292da05e7ad7a0
Signer #1 certificate SHA-1 digest: c5dca53d044a4063cea521802f722b32000788ee
Signer #1 certificate MD5 digest: 5603eadd7d79703921dc93bd4e8bb7a7
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
Signer #1 public key SHA-256 digest: 4da00dd8b96e5b62c618de15516523f8db825fccfbe5551a400dacefd682b7f6
Signer #1 public key SHA-1 digest: 83b6113b06b440dc0babed05fee9c59cdd5e3a8a
Signer #1 public key MD5 digest: 537b8b7fa2d1318275d1be3fd50fe01b
```

## 替换新签名，生成新apk
```
apksigner rotate --out ./lineage --old-signer --ks keystores/old.jks --new-signer --ks keystores/new.jks
apksigner sign --ks keystores/old.jks --next-signer --ks keystores/new.jks --lineage ./lineage --out new-release.apk --v3-signing-enabled true --v4-signing-enabled false app/build/outputs/apk/release/app-release.apk
```

## 验证新生成的new-release.apk的签名
```
apksigner verify -v --print-certs new-release.apk
----------------
Verifies
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): false
Verified using v3 scheme (APK Signature Scheme v3): true
Verified using v4 scheme (APK Signature Scheme v4): false
Verified for SourceStamp: false
Number of signers: 1
Signer #1 certificate DN: CN=New, OU=New, O=New
Signer #1 certificate SHA-256 digest: aec78e0f0d41affa2208a0204a7a4b23c98542b751bbb2c9f77a7d668424bb7c
Signer #1 certificate SHA-1 digest: 3252e09ca86cb6e6c0f34f7f0c40a9961194701a
Signer #1 certificate MD5 digest: d94b5d65658973b0afc06856b2c8d8e5
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
Signer #1 public key SHA-256 digest: 58a974c2d15b41119be7857ece6d29b94488f07bffa541b089687be318954abc
Signer #1 public key SHA-1 digest: 556c0e5ca36cf955bd0d46aa097f870dc449990f
Signer #1 public key MD5 digest: 1f81f2ca1b3a3c0bd4080daa60089e72
```

## 验证覆盖安装
```
adb install app-release.apk
adb install -r new-release.apk
```
从app的主界面可以看到新的apk中同时包含老的签名和新的签名
