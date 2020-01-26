# SakuraNMSTranslator

扔掉反射写NMS代码吧 / Write NMS code without Reflection

(把LdcrUtils的ImplLoader模块拆出来开源了

随便导入nms和craftbukkit用, 这个库使用asm帮你自动替换成服务端的对应版本

有不兼容更改可以创建不同的实现类, 使用SupportedVersion注解后插件会自动识别并选择对应版本

MinecraftVersion.java来自ProtocolLib

## 使用 ##
例程/Sample: [sample-actionbar-api](https://github.com/SakuraKoi/SakuraNMSTranslator/tree/master/sample-actionbar-api)

创建ImplLoader实例
> ImplLoader implLoader = ImplLoader.forPlugin(this, "sakura.kooi.ActionbarAPI.impl", true)

创建接口: [Link](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/ActionBar.java)

创建对应版本实现: [MC_1.8-1.11](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/impl/ActionBar/NMS_v8_v11.java) [MC_1.12+](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/impl/ActionBar/NMS_v12_.java)

实现类需要放在 ImplLoader.forPlugin()时提供的包名.接口名 这个包下
> eg: sakura.kooi.ActionbarAPI.impl(包名).ActionBar(接口名).NMS_v8_v11

`@SupportedVersion`注解: `lowestVersion` (所支持的最低版本)和`highestVersion`(所支持的最高版本), `implVersion`是说明文字

加载实现类
> private static ActionBar impl;
> impl = implLoader.of(ActionBar.class, ActionbarReflection.class);

后面的参数是无匹配版本时的fallback

使用
> impl.sendActionBar(player, message);
