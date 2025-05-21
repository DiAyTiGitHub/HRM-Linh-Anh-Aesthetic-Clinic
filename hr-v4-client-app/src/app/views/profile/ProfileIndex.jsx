import { Grid, useMediaQuery } from "@material-ui/core";
import GlobitsAvatar from "app/common/GlobitsAvatar";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useStore } from "../../stores";
import StaffDetail from "./StaffDetail";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import TabProfileDiagram from "../HumanResourcesInformation/TabContainer/TabProfileDiagram";

function ProfileIndex() {
    const { currentStaff, currentUser, uploadImage, getPageProfileData } = useStore().profileStore;

    const { id } = useParams();
    useEffect(() => {
        getPageProfileData(id);
    }, [id]);

    const isMobileXS = useMediaQuery((theme) => theme.breakpoints.down("xs"));
    const isMD_UP = useMediaQuery((theme) => theme.breakpoints.up("lg"));

    const role =
        currentUser?.roles?.length === 1
            ? currentUser?.roles?.[0]?.name
            : currentUser?.roles?.map((role) => role?.name).join(", ");

    return (
        <section className='staff-root content-index' style={{ height: "calc(100vh + 51px)" }}>
            <GlobitsBreadcrumb routeSegments={[{ name: "Thông tin tài khoản" }]} />
            <Grid container spacing={2} >
                <Grid item lg={3} xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Grid container className='card rounded-1 mt-1' spacing={2}>
                                <Grid item xs={6} lg={12} className='text-center'>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className='flex-center flex'>
                                            <GlobitsAvatar
                                                className='mx-8 text-middle cursor-pointer min-height-avatar'
                                                field='file'
                                                onChange={(_, value) => uploadImage(value)}
                                                disabled
                                                imgPath={currentStaff?.imagePath}
                                                nameStaff={currentStaff?.displayName || "ADMIN"}
                                                name={currentStaff?.displayName || "ADMIN"}
                                            />
                                        </Grid>

                                        {(currentStaff?.displayName || currentStaff?.department?.name) && (
                                            <Grid item xs={12}>
                                                <h4 className='mt-10'>{currentStaff?.displayName}</h4>
                                                <p>{currentStaff?.department?.name}</p>
                                            </Grid>
                                        )}
                                    </Grid>
                                </Grid>

                                <Grid item xs={6} lg={12}>
                                    {(isMobileXS || isMD_UP) && (
                                        <div
                                            className='my-10 w-100'
                                            style={{ borderTop: "3px solid rgba(0,0,0,.1)" }}
                                        />
                                    )}

                                    <p className='text-muted'>Tên tài khoản</p>
                                    <p className='mb-6'>{currentUser?.username}</p>

                                    <span className='text-muted'>Quyền</span>
                                    <p className='mb-6'>{role}</p>

                                    {currentStaff?.email && (
                                        <>
                                            <span className='text-muted'>Email</span>
                                            <p className='mb-6'>{currentStaff?.email}</p>
                                        </>
                                    )}

                                    {currentStaff?.phoneNumber && (
                                        <>
                                            <span className='text-muted'>Số điện thoại</span>
                                            <p className='mb-6'>{currentStaff?.phoneNumber}</p>
                                        </>
                                    )}

                                    {currentStaff?.permanentResidence && (
                                        <>
                                            <span className='text-muted'>Địa chỉ</span>
                                            <p>{currentStaff?.permanentResidence}</p>
                                        </>
                                    )}
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item xs={12} className='card rounded-1 mt-8 mb-54'>
                            <TabProfileDiagram />
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item lg={9} xs={12}>
                    <StaffDetail currentStaff={currentStaff} />
                </Grid>
            </Grid>
        </section>
    );
}

export default memo(observer(ProfileIndex));
