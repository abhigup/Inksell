package utilities;

/**
 * Created by nikhilg on 8/4/15.
 */
public enum ResponseStatus {

        UserNotRegistered,                  //0
        UserAlreadyExist,                   //1
        UserAlreadyRegistered,              //2
        UserAlreadyVerified,                //3
        UserSuccessfullyVerified,           //4
        WrongVerificationCode,              //5
        SomeErrorOccured,                   //6
        PartialUserAddedSuccess,            //7
        PartialUserNotAdded,                //8
        ErrorInTransferringDataInVUTable,   //9
        UserTableUpdateSuccess,             //10
        UserTableNotUpdated,                //11
        UserAddedSuccess,                   //12
        UserAddedFailed,                    //13
        UserNotAuthorized,                  //14
        PostAddedSuccess,                   //15
        PostSummaryAdded,                   //16
        PostSummaryNotAdded,                //17
        UserNotExists,                      //18
        ErrorInUpdationOfVerifyUser,        //19
        PostSummaryDeletedSuccess,          //20
        PostSummaryDeletionFailed,          //21
        PostDeletedSuccess,                 //22
        PostDeletionFailed,                 //23
        PostUpdationSuccess,                //24
        PostUpdationFailed,                 //25
        PostUpdationDenied,                 //26
        CompanyRequestAdded,                //27
        CompanyRequestFailed,               //28
        EmailIdNotPermitted,                //29
        UserNotPermitted,                   //30
        SubscriptionAdded,                  //31
        SubscriptionFailed,                 //32
        UserUriUpdated,                     //33
        UserUriNotUpdated,                  //34
        UnsubscribeSuccess,                 //35
        UnSubscribeFailed,                  //36
        PostSoldOutUpdated,                 //37
        PostSoldOutUpdateFailed ,           //38
        PostSummarySoldOutUpdate,           //39
        PostSummarySoldOutFailed            //40

}
