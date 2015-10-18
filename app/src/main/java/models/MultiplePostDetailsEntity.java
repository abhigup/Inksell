package models;

import java.util.List;

/**
 * Created by Abhinav on 18/10/15.
 */
public class MultiplePostDetailsEntity {

    public String userGuid;

    public String postTitle;

    public List<PostIdWithCategory> associatedPostList;

    public Boolean IsVisibleToAll = false;
}
