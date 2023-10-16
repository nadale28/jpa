package com.example.demo.fileContorl;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContorl {

    @Test
    public void test() throws IOException {
        int cnt = 0;
        for(int i=3; i<=179; i++){
            int noCnt = fileChange("D:\\새 폴더 ("+Integer.toString(i)+")");
            cnt += noCnt;
        }
        System.out.println("cnt = " + cnt);
    }

    public int fileChange(String directoryPath) throws IOException {
        int noCnt = 0;
        String targetDirectoryPath = "D:\\all";
        String[] nameList = {
                "ABBA","ADN","AGD","AGR","ALD","ALSP","AUKG","AQSH","ANB","ATID","AOZ","ALDN","AED","AV","ABD","AKBS",
                "BELL","BKD","BSJR","BHOL","BST",
                "CEAD","CHCH","CLUB","CEMD","CVDX","CXR","CESD","CMI","CETD","CAND","CA","CJ","CM",
                "DANDY","DASD","DMAT","DVDMS","DVDES","DSE","DASS","DDKS","DVRT","DEJU","DKHA",
                "EMAZ","EMBW","ERDM","EMTH","EMAV","EMBZ","EUUD","EMAF","EIKI","EBOD","EMDG",
                "FERA","FFFD","FMR","FUGA","FUFU","FFFS","FD","FAD","FAX",
                "GESD","GIGL","GVH","GVG","GIFD","GS","GAS","GDHH","GG","GKD","GNAB",
                "HBAD","HDKA","HFD","HIMA","HEY","HKD","HKIK","HONE","HOKS","HTHD", "HUNTA","HUNTB","HQIS","HSBD","HHH","HMIX","HHED","HZGD","HMN","HMDN","HEZ",
                "ICD","ISD","IWAN","ITSR","IRO","IENE","IENF","IQQQ",
                "JUE","JJBB","JJDA","JUAN","JUC","JUKD","JUL","JUX","JUQ","JRZR","JUTA","JRZD","JUY","JKNK","JSON","JJBT","JD","JJBK","JUFE","JGAHO","JRZE","JURA",
                "KAM","KAAD","KAZK","KEED","KIR","KSBJ","KMDS","KOP","KNMD","KID","KTDV","KBI","KITAIKE","KEEDX","KOA",
                "LAND","LULU","LIA","LUNS","LHBY",
                "MDMB","MIAD","MAS","MURA","MLMM","MGHT","MTES","MYS","MLSM","MBM","MAC","MATU","MBOX","MBW","MCSR","MDVHJ","MDYD","MTALL","MAD",
                "MEKO","MEYD","MGDN","MLW","MIAA","MHAR","MVSD","MOND","MOKO","MIMK","MOM","MXGS","MMMB","MESU","MOT","MSTG",
                "NBES","NMO","NACR","NACX","NADE","NASH","NASS","NATR","NDRA","NEM","NEO","NEWM","NHDTB","NTSU","NGOD","NKKD","NSPS","NSFS","NWJK","NINE","NXG","NUKA","NOZ",
                "OBA","OBD","OFKU","OKAX","OKSN","OOMN","OVG",
                "PAP","PPPD","PARATHD","PRMJ","PMC","PPPE","PTS","PRED","PPBD",
                "RBD","RCTD","RD","ROE","ROYD","RSE","RUK","RCT","RVG","RUK","RIN","RRE","RUKO","RAF",
                "SERO","SKD","SIKA","SILKBT","SAN","SQIS","SDMUA","SAME","SCD","SCR","SCPX","SDMF","SDMU","SDNM","SIROR","SKKK",
                "SDDE","SKSS","SKSTD","SPRD","SVDVD","SPZ","SY","SW","SSNI","SKMJ","SREN","SFW","SOUL","STAR","SDAM","STARS","SBD","SGV",
                "TANK","TOEN","TNSS","TKD","TMG","TM","TEN","TYVM","TNSPD",
                "UDAK","UAAU","URE","UMD","UMSO",
                "VAGU","VEC","VEMA","VENU","VENX","VRTM","VNDS","VEO","VOD",
                "WAAA","WILL",
                "YLWN","YSAD","YUBA","YRMN","YOB","YSN",
                "XKK",
                "ZMAR","ZOOO"
        };

        // File 객체를 사용하여 폴더를 나타냅니다.
        File directory = new File(directoryPath);

        // 폴더가 존재하는지 확인합니다.
        if (directory.exists() && directory.isDirectory()) {
            // 폴더 내의 파일 목록을 가져옵니다.
            File[] files = directory.listFiles();

            if (files != null) {

                // 파일 목록을 출력합니다.
                for (File file : files) {
                    if (file.isFile()) {
                        String originfileName = file.getName();
                        String fileName = originfileName.replaceAll("-","");
                        fileName = fileName.toUpperCase();
                        String num = "";
                        for (String name : nameList) {
                            Pattern pattern = Pattern.compile(""+name+"(\\d+)");
                            num = matchNumber(pattern,fileName);
                            if(!fileName.equals(num)){
                                num = name+num;
                                break;
                            }
                        }
                        if(!fileName.equals(num)){
                            String ext = getFileExtension(originfileName);
                            String first = "";
                            if(originfileName.startsWith("_")){first = "_";}
                            //System.out.println("zz :"+first+num+"."+ext);
                            Path sourcePath = Paths.get(directoryPath+"\\"+originfileName);
                            Path targetPath = Paths.get(targetDirectoryPath+"\\"+first+num+"."+ext);
                            if(!Files.exists(targetPath)){
                                Files.move(sourcePath, targetPath);
                            }else{
                                System.out.println("중복: " + sourcePath.toString());
                            }
                            
                        }else{
                            noCnt++;
                            System.out.println("미매칭: "+directoryPath+"\\"+fileName);
                        }

                    }
                }
            } else {
                System.out.println("폴더 내에 파일이 없거나 액세스 권한이 없습니다.");
            }
        } else {
            System.out.println("폴더가 존재하지 않습니다.");
        }
        return noCnt;
    }

    public String matchNumber(Pattern pattern, String str){
        Matcher matcher1 = pattern.matcher(str);
        if (matcher1.find()) {
            String number = matcher1.group(1);
            return number;
        }

        return str;
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            // 마지막 점 이후의 문자열을 확장자로 반환
            return fileName.substring(lastDotIndex + 1);
        } else {
            // 확장자가 없는 경우
            return "확장자 없음";
        }
    }
}
