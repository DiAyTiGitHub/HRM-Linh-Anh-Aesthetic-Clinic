import React, {memo, useState} from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import {formatDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom";
import ConstantList from "app/appConfig";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";

function WaitingJobCandidateList() {
    const {waitingJobCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        listWaitingCandidates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListChosen,
        handleOpenRejectPopup,
        handleOpenNotComeToReceivePopup,
        handleOpenReceiveJobPopup,
    } = waitingJobCandidateStore;

    const history = useHistory();

    function handleUpdateApplicant(applicant) {
        //link to new page like a staff
        history.push(ConstantList.ROOT_PATH + `candidate/` + applicant?.id + "?isFromWaitingJobCandidate=true");
    }

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            minWidth: "100px",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Chi tiết" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleUpdateApplicant(rowData);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Thao tác khác" placement="top">
                            <IconButton
                                className="ml-8"
                                size="small"
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}
                            >
                                <MoreHorizIcon/>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Mã ứng viên",
            field: "candidateCode",
            minWidth: "150px",
        },
        {
            title: "Họ tên",
            field: "displayName",
            minWidth: "150px",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
        </span>
            ),
        },
        {
            title: "SĐT",
            field: "phoneNumber",
            minWidth: "150px",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
              {rowData?.phoneNumber}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Kế hoạch tuyển dụng",
            field: "recruitment",
            minWidth: "150px",
            render: rowData => (
                <>
                    {rowData?.recruitmentPlan && (
                        <span className="pr-8">
              {rowData?.recruitmentPlan?.name}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày nộp hồ sơ",
            field: "submissionDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.submissionDate && (formatDate("DD/MM/YYYY", rowData?.submissionDate))}
        </span>
            ),
        },
        {
            title: "Ngày thi tuyển",
            field: "interviewDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.interviewDate && (formatDate("DD/MM/YYYY", rowData?.interviewDate))}
        </span>
            ),
        },
        {
            title: "Ngày nhận việc",
            field: "onboardDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.onboardDate && (formatDate("DD/MM/YYYY", rowData?.onboardDate))}
        </span>
            ),
        },
        {
            title: "Đơn vị tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.organization.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Phòng ban tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.hrDepartment.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Vị trí tuyển dụng",
            field: "positionTitle.name",
            align: "left",
            minWidth: "150px",
        },
    ];

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    return (
        <>
            <GlobitsTable
                selection
                data={listWaitingCandidates}
                handleSelectList={handleSelectListChosen}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className="py-0"
                >
                    {/* NHẬN ỨNG VIÊN VÀO LÀM VIỆC */}
                    {selectedRow?.onboardStatus != LocalConstants.CandidateOnboardStatus.ONBOARDED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenReceiveJobPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "green"}}>
                                alarm_on
                            </Icon>

                            <span className="ml-4">
                Nhận việc
              </span>
                        </MenuItem>
                    )}


                    {/* ỨNG VIÊN KHÔNG ĐẾN NHẬN VIỆC */}
                    {selectedRow?.onboardStatus != LocalConstants.CandidateOnboardStatus.NOT_COME.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenNotComeToReceivePopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "red"}}>
                                alarm_off
                            </Icon>

                            <span className="ml-4">
                Không nhận việc
              </span>
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(WaitingJobCandidateList));
