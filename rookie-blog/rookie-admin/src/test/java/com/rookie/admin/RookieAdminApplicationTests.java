package com.rookie.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class RookieAdminApplicationTests {

    @Test
    void contextLoads() throws IOException {
        // 下载地址列表
        String[] urls = {
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/13/686aa72f290447e1a68d993ba0c63e58@追觅代垫--新丝璐.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/8ccb0ed00dcf4babbf549769c7bf6c1e@税金代垫账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/13/f13418712a9142e08badb518c8f7a4ed@25.01-艾姆勒1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/2bc567b91198450e9c77d10fbb6d0558@鹏城海25.01.国际物流费追觅创新RMB.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/14/5747f05c41f84c1fbdeeb01dfb6e0115@25.01-艾姆勒1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/8a32b39f20574f3192b925fcf00325c4@鹏城海25.01.代垫进口税金追觅创新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/7292b79528f44d6fb7d594660e71d362@2025年1月税金账单-2.15.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/2d9c31c2cf634d28845f6e0238593b46@1月国际运费账单-追觅-2.17.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/b346cf6b0e0d47c7827576414e209bdf@1月国际运费账单-追觅-2.17.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/25/2fb04849d16e479dbbea065d5142976a@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/25/c8ce5dd38ec84a83a7cc4dfe6e860f48@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/11/142c3f38c6f044d6a049164cfcce25d9@2025.01月追觅国际运费账单-新丝路4.11.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/28/ff37973c0b8e4d15a2773b9a506819e5@追觅-谷仓24.12-25.1月头程账单最终版本.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/28/eff922ebb36f4dcbbb07dfa7978711ec@追觅-谷仓24.10-11月头程账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/6cabe096fd9f45f48369898561582b67@2025.01月追觅国际运费账单-新丝路.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/01/e5992e0b4d1845d68ec77b429c68665d@肯亚-追觅2025年1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/01/c2785f87da804770889c798a252fc90d@肯亚-追觅2025年1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/01/cb08e74c087e45a4bc968746005e4ed6@肯亚-追觅2025.1月税金账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/02/247ca53f4c95474ba5658e5e8204fec0@追觅-谷仓24.12-25.1月头程账单最终版本.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/f22ed9d296dd436980d5317b586a3f84@1月追觅-贝业账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/c1e098bfaeb24a52af44e06db8ccb036@1月追觅-贝业账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/b577a2142952422b9a213fb96bc882f6@核对-优捷1月税金账单-1100抬头.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/27a0f639aed6404a867fed52321fec4f@核对-优捷1月税金账单-1100抬头.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/930798fbc6b04476b414b20deef6b030@追觅订舱单2025-1月原始数据-实际账单-追觅约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/23ec54bfee86426b9d3cdd23e34f5405@追觅订舱单2025-1月原始数据-智洋约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/23b6484043f242d6b25ea2741891d4f9@世航快递1月税金账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/abe663dc22be4103b10f31641955ce95@世航01月税金账单TMS.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/cb5a762c6dd74048bc3fa07dd704a82c@世航01月税金账单TMS.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/7d8a76ab48e04a74a7780d3ece8eeee0@追觅订舱单2025-1月原始数据-实际账单-追觅约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/de7fade31da14b76b5d46cb8a13393bc@追觅订舱单2025-1月原始数据-智洋约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/440d0a182c644c9f9e2b98befdf50ab4@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/3e1a1fe67b344cebb973003a84d72b22@世航01月账单（OA）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/9c52ba75e82c449eaba2a050ffa93dc6@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/ad91d499f2d04e55aacb4dfb2a431f77@1月恒捷-追觅订舱单更新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/6d8dc9e95862496d969a15f223d6eb1e@1月恒捷-追觅订舱单更新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/13/371cbf2c3cc24424b0ae0bdcfdc073c7@账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/13/8c049049cce14badaf2657a37b520b42@报价单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/19/197c0e52a1574232b8829e789432cc6d@货物运输险协议业务申报清单追创苏州.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/03/2a114dab9bcc4fb58171a26ec8750400@2025年01月贝业-内装对账单-2.24修改版-追觅创新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/16/ed8baceaad114abf92685697d0dfdb0e@格罗-创新25.01月帐单（（TMS系统订舱日期截止25-01-17）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/15/37acc7316d4648e38d8b0d9445bcc175@追觅古德2025.01月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/09/ea5cd2a8d2f04e39b0efc110ed93bf62@2025年1月海外仓德速费用.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/09/862e03306c4f41dfb5115c9834ee8c4b@25年1月份海外仓对账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/10/8f602c67f4ee4eff957902127d0d1ca5@追觅贸易202501账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/10/c36fe5127a964963b68882f739520c61@荣昇美国仓渠道报价V06.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/1e3d5e37d9bd47c784f6888ec0a9096d@949660-德国仓1月库内尾程账单—核对.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/0f32b40e86d247c599b699901dad64dc@HFINS应付发票行导入4.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/79273d9b02fa40da91777a7f1b395f07@海外仓仓储库内服务报价20240101.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/7b038a39229343b4ad1193f79e622721@海外仓末端派送价格_德国V2价20240520.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/26/42995084825848a399cb40693a45121e@1月账单合计G14817G14818-核对.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/26/f874d1c16fbd4082ac0e3afa70c4d611@20250101谷仓法国仓物流服务-FRF3-追觅.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/26/f9bcb929b04e4383b89719b91003313b@20240828-谷仓海外仓仓储服务报价-追觅-待生效.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/26/4d871bf85cff4bdc9b0d8e31b5c3c474@G7733加币1月.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/26/e41ce653ef05463b94d65cb68e288f05@G7733USD1月.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/9c4346966c024a69a8ec1dddbc3d9f18@CTSbill.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/14/f534cbdfa1954af18b744a422e787fae@优捷海外段1月费用核对.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/14/0a8296f8f8af4e09bfac087b872817d0@优捷海外段1月报价.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/27/be8ca7fccab940d9a146ade5eba68ef3@G141701月.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/4a8386e08d9b4c9caac0dae1579c8a01@鹏城海25.01.国际物流费追觅新加坡.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/13/9c61aeb2504b4e0ba3aeed129b89558e@25.01-艾姆勒1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/18/24fc76dc4b7248d2b3d1cd5e4e7d1bae@锦达1月账单预估-已登记.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/9308dfb490c447788d87df743fefc6ab@鹏城海25.01.国际物流费绍兴月石RMB.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/f0f7db3189bd4d329908387ca6e5729a@鹏城海25.01.国际物流费绍兴月石USD.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/19/a463f3f4f136403fb5af49f16f5314b1@鹏城海25.01.代垫进口税金绍兴月石.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/cb485d41d65847fd993357a8446d182c@2025年1月税金账单-2.15.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/24/f7f94b60cc4d442c8ae7c834ee98e750@1月国际运费账单-追觅-2.17.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/25/4a3af410cb8b42fb96146f3542577f45@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/25/a7cc61ae9ebe4d59a69337f117e788e4@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/11/d2e0e41624f547a299f3c449db00a503@2025.01月追觅国际运费账单-新丝路4.11.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/8c8d68d9802248a8bf572d9c8a0cb750@2025.01月追觅国际运费账单-新丝路.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/01/118dab62eadc44028219ced5e63576d9@肯亚-月石2025.1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/f35a28fb37364fd6b797ec962844d1c9@世航快递1月税金账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/067475d62477402eadb2c1b2e8c5f3d4@追觅订舱单2025-1月原始数据-实际账单-追觅约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/f5ba15dfeebc4c9398ffeddbf940b6d0@追觅订舱单2025-1月原始数据-智洋约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/3feee605bee04dc59c689364b10828a4@追觅订舱单2025-1月原始数据-实际账单-追觅约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/7ca530fe564946e4b9cc08919472c8f1@追觅订舱单2025-1月原始数据-智洋约号.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/f769f65833d0494eaf21cb623ff0a975@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/e5bb0881266b45b0be35816cb9bd676b@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/c7dce8c256d84528ae5dbb6878321e12@1月恒捷-星旷订舱单更新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/6dac972072cf4e8c99ac9431729f5af5@1月恒捷-星旷订舱单更新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/19/91163a5efa1f473c81ea96d1962660ed@货物运输险协议业务申报清单星旷科技（原绍兴月石）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/06/fce891e514714e208fd765bac301cc24@核对-优捷1月国际物流账单-4500抬头.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/11/ffe18417869d467ca5249d7a3a0cb3f0@2025年01月贝业-内装对账单-2.24修改版-星旷创新.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/15/70ddb66843d0481e803892f7056033c2@星旷古德2025.01月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/27/e645cf52740d4e2382db6528fe6b79a5@1月账单G14367补运费）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/27/d559a743a6814400b7dddda8b189529a@20250217谷仓美国仓储服务-USF52（追觅）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/18/f72103cbd87d4fa79c54e759868cdbe6@锦达1月账单预估-已登记.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/02/25/8640d569a3404db59d270a6197a6f073@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/11/93cc0260b8734b8595c1ebf017d8c051@2025.01月追觅国际运费-中外运.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/11/3853d962a744439cae29d78118bc53c3@2025.01月追觅国际运费账单-新丝路4.11.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/06/fc169ef3f2404d7e96205af0498313a0@2025.01月追觅国际运费账单-新丝路.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/15/46a6a8a2f4d54b0384756f3aadb097f2@肯亚-可庭2025.1月账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/10/6230ac47e6254f5788e314ef78100771@1月可庭-贝业账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/10/4ac1e886fea4430ea644345678e10c07@1月可庭-贝业账单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/a71aa65e12664cac87354dda142553cd@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/2402960211fa40a198f606ea24211e8c@世航01月账单（TMS）.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/eb169e7965354c2a845a4dd412e56a25@1月恒捷-可庭订舱单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/12/de34d07e406b4ade8b68c753145e82b7@1月恒捷-可庭订舱单.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/19/53d59310cb7f4db08e8b28bbcf093738@货物运输险协议业务申报清单可庭科技.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/09/a8afd540613340e6ba45aa4531c11e85@2025年01月贝业-内装对账单-2.24修改版-可庭科技.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/15/f88ecef99a6447cfadbf8e58a3991c90@核对-优捷1月税金账单-6100抬头.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/04/15/eb2d0cf33cb54c0a9ad964f21531fb0c@核对-优捷1月国际物流账单-6100抬头.xlsx",
                "https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/03/11/11fc790950184039852ce5d2d91ae55d@1月国际运费账单-追觅-2.17奎洛.xlsx",


        };

        // 目标文件夹路径
        String targetFolder = "downloads";

        // 创建目标文件夹
        Path targetPath = Paths.get(targetFolder);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        // 遍历URL列表并下载文件
        for (String url : urls) {
            try {
                URL fileUrl = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) fileUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);

                // 获取文件名
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                Path filePath = targetPath.resolve(fileName);

                // 下载文件
                try (InputStream in = httpURLConnection.getInputStream();
                     OutputStream out = new FileOutputStream(filePath.toFile())) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("文件已下载到: " + filePath);
            } catch (IOException e) {
                System.err.println("下载文件时出错: " + url);
                System.err.println("错误信息: " + e.getMessage());
                System.err.println("请检查链接的合法性或稍后重试。");
            }
        }
    }


}
