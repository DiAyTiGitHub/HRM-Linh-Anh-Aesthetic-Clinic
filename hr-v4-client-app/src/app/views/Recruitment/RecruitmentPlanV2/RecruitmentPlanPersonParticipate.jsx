import GlobitsTable from "../../../common/GlobitsTable";
import React, {useEffect, useState} from "react";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import {formatDate} from "../../../LocalFunction";
import LocalConstants, {PositionRelationshipType} from "../../../LocalConstants";
import {useTranslation} from "react-i18next";
import ChooseUsingStaffSection from "../../User/UsingAccountStaff/ChooseUsingStaffSection";
import {Checkbox, Grid, Icon, IconButton} from "@material-ui/core";
import {useFormikContext} from "formik";
import {toast} from "react-toastify";

const RecruitmentPlanPersonParticipate = ({open, handleClose, value}) => {
    const {t} = useTranslation();
    const {values} = useFormikContext()
    const [staff, setStaff] = useState([])
    const handleRemove = (no) => {
        setStaff((prevStaff) =>
            Array.isArray(prevStaff)
                ? prevStaff.filter((_, index) => index !== no)
                : []
        );
        value.participatingPeople = value?.participatingPeople?.filter((_, index) => index !== no);
    };

    let columns = [
        {
            title: t("general.action"),
            align: "center",
            render: (rowData) => (
                <IconButton size='small' onClick={() => handleRemove(rowData?.tableData?.id)}>
                    <Icon fontSize='small' color='error'>
                        delete
                    </Icon>
                </IconButton>
            ),
        },
        {
            title: "Người đưa ra quyết định",
            align: "center",
            render: (rowData) =>
                (
                    <>
                        <Checkbox
                            checked={staff?.[rowData?.tableData?.id]?.judgePerson}
                            className="p-6"
                            onChange={(_, checked) => {
                                if (value.participatingPeople.some(item => item.id !== rowData.id && item.judgePerson)) {
                                    if (checked) {
                                        toast.warning("Chỉ có 1 người đưa ra quyết định")
                                    } else {
                                        value.participatingPeople[rowData?.tableData?.id].judgePerson = checked
                                        const newParticipatingPeople = [...value.participatingPeople];
                                        setStaff((prev) => ({
                                            ...prev,
                                            participatingPeople: newParticipatingPeople,
                                        }));
                                    }
                                } else {
                                    value.participatingPeople[rowData?.tableData?.id].judgePerson = checked
                                    const newParticipatingPeople = [...value.participatingPeople];
                                    setStaff((prev) => ({
                                        ...prev,
                                        participatingPeople: newParticipatingPeople,
                                    }));
                                }
                            }}
                        />
                    </>
                )
        },
        {
            title: "Mã nhân viên",
            field: "staffCode",
            align: "center",
            render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
        },
        {
            title: "Nhân viên",
            minWidth: "200px",
            render: (rowData) => (
                <>
                    {rowData.displayName && (
                        <p className='m-0'>
                            <strong>{rowData.displayName}</strong>
                        </p>
                    )}

                    {rowData.birthDate && (
                        <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>
                    )}

                    {rowData.gender && (
                        <p className='m-0'>
                            Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}
                        </p>
                    )}

                    {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
                </>
            ),
        },
        {
            title: "Thông tin liên hệ",
            field: "info",
            minWidth: "200px",
            render: (rowData) => (
                <>
                    {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

                    {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}

                    {/* {rowData.currentResidence && (
            <p className="m-0">Nơi ở hiện tại: {rowData.currentResidence}</p>
          )} */}
                </>
            ),
        },
        {
            title: "Trạng thái nhân viên",
            field: "status.name",
            align: "left",
            minWidth: "150px",
            render: (rowData) => <span className='pr-6'>{rowData?.status?.name}</span>,
        },
        {
            title: "Quản lý trực tiếp",
            align: "left",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {rowData?.currentPosition?.relationships
                        ?.filter(
                            (item) =>
                                item?.supervisor?.name &&
                                item?.relationshipType === PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.value
                        )
                        ?.map((item, index) => (
                            <>
                                {index > 0 && <br/>}
                                <span className='pr-6'>
                                    - {item?.supervisor?.name}
                                    {item?.supervisor?.staff?.displayName
                                        ? ` (${item.supervisor.staff.displayName})`
                                        : ""}
                                </span>
                            </>
                        ))}
                </>
            ),
        },
        {
            title: "Đơn vị",
            field: "organization.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.organization?.name}</span>,
        },
        {
            title: "Phòng ban",
            field: "department.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => (
                <>
                    {rowData?.department?.name && <p className='m-0'>{rowData?.department?.name}</p>}
                    {rowData?.department?.code && <p className='m-0'>({rowData?.department?.code})</p>}
                </>
            ),
        },
        {
            title: "Chức danh",
            field: "positionTitleName",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.positionTitle?.name}</span>,
        },

        {
            title: "Nơi ở hiện tại",
            field: "currentResidence",
            align: "left",
            minWidth: "180px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
        },
        {
            title: "Mã số BHXH",
            field: "socialInsuranceNumber",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.socialInsuranceNumber}</span>,
        },
        {
            title: "Trạng thái hồ sơ",
            field: "staffDocumentStatus",
            align: "left",
            minWidth: "120px",
            render: (rowData) => (
                <span className='pr-6'>
                    {LocalConstants.StaffDocumentStatus.getListData().find(
                        (item) => item.value === rowData?.staffDocumentStatus
                    )?.name || ""}
                </span>
            ),
        },
    ];

    useEffect(() => {
        values.replacedPerson = null
        setStaff(value?.participatingPeople)
    }, [value]);

    useEffect(() => {
        if (values.replacedPerson) {
            if(!value.participatingPeople) {
                value.participatingPeople = []
            }
            if (value.participatingPeople.some(item => item.id === values.replacedPerson.id)) {
                toast.warning("Nhân viên này đã tham gia")
            } else {
                value.participatingPeople = [...value?.participatingPeople, values?.replacedPerson]
                setStaff(value.participatingPeople)
            }
        }
        values.replacedPerson = null
    }, [values.replacedPerson]);

    return (
        <>
            <GlobitsPopupV2
                open={open}
                onClosePopup={handleClose}
                scroll={"paper"}
                size="md"
                noDialogContent
                title={t("recruitmentPlan.personParticipate") + " - " + value?.name ?? ""}
            >
                <Grid container style={{alignItems: 'end'}}>
                    <Grid item sm={6} xs={12} md={4} style={{padding: "10px"}}>
                        <ChooseUsingStaffSection
                            label='Nhân viên tham gia'
                            placeholder='Chọn nhân viên tham gia'
                            name='replacedPerson'
                        />
                    </Grid>
                    <GlobitsTable
                        data={value?.participatingPeople}
                        columns={columns}
                        nonePagination
                    />
                </Grid>
            </GlobitsPopupV2>
        </>
    );
}
export default RecruitmentPlanPersonParticipate;