import React, {memo, useEffect} from "react";
import {
    makeStyles,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {formatDate, getDate} from "app/LocalFunction";
import {useStore} from "../../../stores";
import PersonCertificateList from "../../HumanResourcesInformation/PersonCertificate/PersonCertificateList";
import GlobitsTable from "../../../common/GlobitsTable";
import moment from "moment";
import {observer} from "mobx-react";

const useStyles = makeStyles((theme) => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "8px",
    },
}));

function StaffApplyProcess() {
    const {t} = useTranslation();
    const {candidateStore, staffStore} = useStore();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {
        getExistCandidateProfileOfStaff,
        candidateProfilesOfStaff,
        resetStore
    } = candidateStore;

    let columns = [
        {
            title: "Mã ứng viên",
            field: "candidateCode",
            minWidth: "150px",
            render: row => <span>{(row?.candidateCode)}</span>
        },
        {
            title: "Đơn vị",
            field: "organization",
            minWidth: "150px",
            render: row => <span>{(row?.organization?.name)}</span>
        },
        {
            title: "Phòng ban",
            field: "department",
            minWidth: "150px",
            render: row => <span>{(row?.department?.name)}</span>
        },
        {
            title: "Vị trí",
            field: "postion",
            minWidth: "150px",
            render: row => <span>{(row?.postion?.name)}</span>
        },
        {
            title: "Ngày nộp hồ sơ",
            field: "submissionDate",
            minWidth: "150px",
            render: row => <span>{getDate(row?.submissionDate)}</span>
        },
        {
            title: "Nơi ở",
            field: "currentResidence",
            minWidth: "150px",
            render: row => <span>{row?.currentResidence}</span>
        },
    ];

    useEffect(function () {
        if (values?.id) {
            getExistCandidateProfileOfStaff(values?.id);
        }
    }, [values?.id]);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {candidateProfilesOfStaff?.length > 0 ? (
                    <GlobitsTable
                        data={candidateProfilesOfStaff}
                        columns={columns}
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Chưa có quá trình ứng tuyển nào!
                    </h5>
                )}
            </div>
        </div>
    );
}

export default memo(observer(StaffApplyProcess))