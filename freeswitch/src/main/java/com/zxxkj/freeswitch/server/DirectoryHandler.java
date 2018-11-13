//package com.zxxkj.freeswitch.server;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class DirectoryHandler implements HttpHandler {
//    @Override
//    public void handle(HttpExchange httpExchange) throws IOException {
//        //String uri=exc.getRequestURI().toString();
//        //获得输入流
//        BufferedReader reader = new BufferedReader(new InputStreamReader(exc.getRequestBody()));
//        //用于存储请求信息  请求参数
//        String valueString = null;
//        StringBuilder sbf = new StringBuilder();
//        while ((valueString = reader.readLine()) != null) {
//            sbf.append(valueString);
//        }
//        String param = sbf.toString();
//        //System.out.println(param);
//        String[] str_ = param.split("&");
//        @SuppressWarnings("unused")
//        String section = null;
//        String req_key = null;
//        String req_user = null;
//        String req_domain = null;
//        @SuppressWarnings("unused")
//        String req_ip = null;
//        String req_callout = null;
//        String req_pswd = "1234";
//        for (int i = 0; i < str_.length; i++) {
//            String[] str2_ = str_[i].split("=");
//            switch (str2_[0]) {
//
//                case "section":
//                    section = str2_[1];
//                    break;
//                case "key":
//                    req_key = str2_[1];
//                    break;
//                case "user":
//                    req_user = str2_[1];
//                    req_callout = str2_[1];
//                    break;
//                case "domain":
//                    req_domain = str2_[1];
//                    break;
//                case "ip":
//                    req_ip = str2_[1];
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        //此处查询数据库
//        DirectoryEntity directorys = directoryManageServerBusiness.selectExtensions(req_user, req_domain);
//        req_pswd = directorys.get(0).getExtensionPswd();
//        responseMessage = "<document type='freeswitch/xml'>" +
//                        "<section name='directory'>" +
//                        "   <domain name='" + req_domain + "'>" +
//                        "     <params>" +
//                        "        <param name='dial-string' " +
//                        "       value='{presence_id=${dialed_user}@${dialed_domain}}${sofia_contact(${dialed_user}@${dialed_domain})}'/>" +
//                        "     </params>" +
//                        "     <groups>" +
//                        "       <group name='default'>" +
//                        "        <users>" +
//                        "          <user id='" + req_user + "'>" +
//                        "           <params>" +
//                        "              <param name='password' value='" + req_pswd + "'/>" +
//                        "              <param name='vm-password' value='" + req_pswd + "'/>" +
//                        "           </params>" +
//                        "           <variables>" +
//                        "              <variable name='toll_allow' value='domestic,international,local'/>" +
//                        "              <variable name='accountcode' value='" + req_user + "'/>" +
//                        "              <variable name='user_context' value='default'/>" +
//                        "              <variable name='effective_caller_id_name' value='" + req_user + "'/>" +
//                        "              <variable name='effective_caller_id_number' value='" + req_user + "'/>" +
//                        "              <variable name='directory-visible' value='true'/>" +
//                        "              <variable name='directory-exten-visible' value='true'/>" +
//                        "              <variable name='limit_max' value='1'/>" +
//                        "              <variable name='outbound_caller_id_name' value='" + req_callout + "'/>" +
//                        "              <variable name='outbound_caller_id_number' value='" + req_callout + "'/>" +
//                        "              <variable name='callgroup' value='techsupport'/>" +
//                        "           </variables>" +
//                        "         </user>" +
//                        "        </users>" +
//                        "       </group>" +
//                        "      </groups>" +
//                        "    </domain>" +
//                        " </section>" +
//                        "</document>";
//    }else{
//        responseMessage = " <document type='freeswitch/xml'>" +
//                        "  <section name='directory'>" +
//                        "  </section>" +
//                        " </document>";
//    }
//    //回应信息
//            exc.sendResponseHeaders(HttpURLConnection.HTTP_OK,responseMessage.getBytes().length);
//    OutputStream out = exc.getResponseBody();
//            out.write(responseMessage.getBytes());
//            out.flush();
//            exc.close();
//
//}
//}
