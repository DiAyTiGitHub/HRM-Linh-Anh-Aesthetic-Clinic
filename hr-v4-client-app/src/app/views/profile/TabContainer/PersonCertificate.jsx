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
import {formatDate} from "app/LocalFunction";
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

function PersonCertificate() {
    const {t} = useTranslation();
    const {personCertificateStore} = useStore();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {
        getAllPersonCertificateByPerson,
        setPersonId,
        personCertificateList,
    } = personCertificateStore;

    let columns = [
        {
            title: t("certificate.name"),
            field: "name",
            minWidth: "150px",
        },
        {
            title: t("certificate.level"),
            field: "level",
            minWidth: "150px",
        },
        {
            title: t("certificate.issueDate"),
            field: "issueDate",
            minWidth: "150px",
            render: (rowData) => rowData?.issueDate ? moment(rowData?.issueDate).format("DD/MM/YYYY") : "",
        },
    ];

    useEffect(() => {
        setPersonId(values?.id)
        if (values?.id) {
            getAllPersonCertificateByPerson();
        }
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {personCertificateList?.length > 0 ? (
                    <GlobitsTable
                        data={personCertificateList}
                        columns={columns}
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Không có chứng chỉ/chứng nhận nào!
                    </h5>
                )}
            </div>
        </div>
    );
}

export default memo(observer(PersonCertificate))