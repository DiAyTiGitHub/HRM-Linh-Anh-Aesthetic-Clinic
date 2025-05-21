import { observer } from "mobx-react";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { memo, useEffect } from "react";
import { useStore } from "../../stores";
import "../Task/_task.scss";

function UserInOrgSection(props) {
    const { organizationStore } = useStore();
    const {
        getCurrentOrganizationOfCurrentUser,
        currentCompanyInfo
    } = organizationStore;

    return (
        <>
            {currentCompanyInfo?.users && (currentCompanyInfo?.users?.length) > 0 && (
                <Grid item xs={12} className="pb-18">
                    <strong>Danh sách tài khoản người dùng hoạt động trong công ty:</strong>
                    <div className="orgUserContainer">
                        {
                            (currentCompanyInfo?.users)?.map((item, index) => {
                                const user = item?.user;
                                const displayName = user?.displayName;
                                const username = user?.username;

                                return (
                                    <p
                                        key={index}
                                        className="tag text-white"
                                        style={{
                                            display: "inline-block",
                                            borderRadius: "4px",
                                            padding: "5px",
                                            color: "#fff",
                                            margin: 3,
                                            backgroundColor: "#3e8383"
                                        }}
                                    >
                                        {displayName} {username && (" (" + username) + ")"}
                                    </p>
                                );
                            })
                        }
                    </div>
                </Grid>
            )}
        </>
    );
}

export default memo(observer(UserInOrgSection));