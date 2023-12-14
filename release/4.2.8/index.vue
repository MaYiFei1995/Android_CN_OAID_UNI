<template>
	<view class="content">
		<text class="title">**请确保用户同意隐私政策后再调用注册方法**</text>
		<button @click="register">注册</button>
		<button @click="getClientId">ClientId</button>
		<button @click="getIMEI">IMEI</button>
		<button @click="getOAID">OAID</button>
		<button @click="getAndroidID">AndroidID</button>
		<button @click="getWidevineID">WidevineID(不推荐使用)</button>
		<button @click="getPseudoID">PseudoID</button>
		<button @click="getGUID">GUID</button>
		<text class="bottom-version">SDK-Version:{{sdkVer}}</text>
	</view>
</template>

<script>
	var oaid = uni.requireNativePlugin("Android_CN_OAID")
	export default {
		data() {
			return {
				sdkVer: oaid.getVersion(),
			}
		},
		onLoad() {

		},
		methods: {
			// 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
			// * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
			register() {
				// param1为1时tryWidevine，由于兼容问题，IMEI/MEID及OAID获取失败后默认不尝试获取WidevineID
				oaid.register(0, (ret) => {
					if (ret.isSuccess) {
						this.showModal('注册成功，clientId: ' + ret.clientId)
					} else {
						this.showModal('注册失败，错误信息为: ' + ret.error)
					}
				})
			},

			// 使用该方法获取客户端唯一标识，需要先调用{@link #register}预取
			getClientId() {
				// param returnRaw 返回的是否是原始值，默认为MD5加密后的值
				this.showModal('clientId: ' + oaid.getClientId(1))
			},

			// 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI/MEID，Android 10+ 非系统应用则不再允许获取 IMEI。
			getIMEI() {
				this.showModal('IMEI: ' + oaid.getIMEI())
			},

			// 使用该方法获取OAID/AAID，需要先在{@link Application#onCreate()}里调用{@link #register}预取
			getOAID() {
				this.showModal('OAID: ' + oaid.getOAID())
			},

			// 获取AndroidID
			getAndroidID() {
				this.showModal('AndroidID: ' + oaid.getAndroidID())
			},

			// 获取数字版权管理设备ID
			// 不推荐使用，因为在某些手机上调用会莫名其妙的造成闪退或卡死，还难以排查到原因
			getWidevineID() {
				this.showModal('WidevineID: ' + oaid.getWidevineID())
			},

			// 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
			// 不会为空，但会有一定的概率出现重复
			getPseudoID() {
				this.showModal('PseudoID: ' + oaid.getPseudoID())
			},

			// 随机生成全局唯一标识并存到{@code SharedPreferences}、{@code ExternalStorage}及{@code SystemSettings}。
			// GUID不会为空，但应用卸载后会丢失
			getGUID() {
				this.showModal('GUID: ' + oaid.getGUID())
			},

			showModal(msg) {
				uni.showModal({
					content: msg
				})
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}

	.bottom-version {
		position: absolute;
		bottom: 30rpx;
		left: 0;
		width: 100%;
		text-align: center;
		font-size: 26rpx;
		color: #999;
	}
</style>