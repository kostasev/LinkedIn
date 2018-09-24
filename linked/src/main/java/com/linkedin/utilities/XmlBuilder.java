package com.linkedin.utilities;

import com.jamesmurty.utils.XMLBuilder;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.String.valueOf;

public class XmlBuilder {
    static public XMLBuilder getXml(ResultSet user, ResultSet post, ResultSet comments, ResultSet likes) {
        try {
            XMLBuilder xmlBuilder = XMLBuilder.create("Linkedin")
                    .e("user");

            while (user.next()) {
                if (user.getString("email").equals("root")) continue;
                xmlBuilder = xmlBuilder.e("User")
                        .e("UserID")
                        .t(valueOf(user.getInt("iduser")))
                        .up()
                        .e("Email")
                        .t(user.getString("email"))
                        .up()
                        .e("FirstName")
                        .t(user.getString("name"))
                        .up()
                        .e("LastName")
                        .t(user.getString("surname"))
                        .up()
                        .e("PhoneNumber")
                        .t(user.getString("phone") == null ? "N/A" : user.getString("phoneNumber"))
                        .up()
                        .up();
            }

            xmlBuilder = xmlBuilder.up();

            xmlBuilder = xmlBuilder.e("post");

            while(post.next()) {
                xmlBuilder = xmlBuilder.e("Post")
                        .e("idpost")
                        .t(valueOf(post.getInt("idpost")))
                        .up()
                        .e("Author")
                        .t(valueOf(post.getInt("author")))
                        .up()
                        .e("Text")
                        .t(valueOf(post.getString("post")))
                        .up();
            }

            xmlBuilder = xmlBuilder.up();


            xmlBuilder = xmlBuilder.e("Comments");

            while (comments.next()) {
                xmlBuilder = xmlBuilder.e("Comment")
                        .e("CommentID")
                        .t(valueOf(comments.getInt("idcomment")))
                        .up()
                        .e("UserID")
                        .t(valueOf(comments.getInt("author")))
                        .up()
                        .e("PostID")
                        .t(valueOf(comments.getInt("idpost")))
                        .up()
                        .e("Comment")
                        .t(comments.getString("text"))
                        .up()
                        .up();
            }

            xmlBuilder = xmlBuilder.up();
            // SECTION END

            xmlBuilder = xmlBuilder.e("likes");

            while (likes.next()) {
                xmlBuilder = xmlBuilder.e("Message")
                        .e("PostID")
                        .t(valueOf(likes.getInt("idpost")))
                        .up()
                        .e("SenderID")
                        .t(valueOf(likes.getInt("iduser")))
                        .up()
                        .up();
            }

            xmlBuilder = xmlBuilder.up();

            return xmlBuilder;
        } catch ( SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}