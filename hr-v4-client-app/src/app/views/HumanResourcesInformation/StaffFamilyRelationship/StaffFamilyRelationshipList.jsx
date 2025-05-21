import React from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton } from "@material-ui/core";
import { observer } from "mobx-react";
import moment from "moment/moment";
import CheckIcon from "@material-ui/icons/Check";
import { getDate } from "app/LocalFunction";

function MaterialButton(props) {
    const { item } = props;
    return (
        <div>
            <IconButton size='small' onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize='small' color='primary'>
                    edit
                </Icon>
            </IconButton>
            <IconButton size='small' onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize='small' color='error'>
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function PersonCertificateList() {
    const { staffFamilyRelationshipStore } = useStore();
    const { t } = useTranslation();

    const {
        staffFamilyRelationshipList,
        handleDelete,
        handleStaffFamilyRelationshipEdit,
        handleSetSelectListStaffFamilyRelationship
    } = staffFamilyRelationshipStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleStaffFamilyRelationshipEdit(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert(t("general.alert.callSelected") + rowData.id); // Dịch thông báo
                        }
                    }}
                />
            ),
        },
        {
            title: t("relatives.name"),
            field: "fullName",
            minWidth: "150px",
        },
        {
            title: t("relatives.dob"),
            field: "birthDate",
            minWidth: "150px",
            render: (rowData) => rowData?.birthDate ? moment(rowData?.birthDate).format("DD/MM/YYYY") : "",
        },
        {
            title: t("relatives.relative"),
            field: "relative",
            minWidth: "150px",
            render: (rowData) => rowData?.familyRelationship?.name ? rowData?.familyRelationship?.name : "",

        },
        {
            title: t("relatives.taxCode"),
            field: "taxCode",
            minWidth: "150px",
        },
        {
            title: t("relatives.dependent"),
            field: "isDependent",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => rowData?.isDependent ? <CheckIcon fontSize="small" style={{ color: "green" }} /> : "",
        },
        {
            title: t("relatives.dependentDeductionFromDate"),
            field: "dependentDeductionFromDate",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => getDate(rowData?.dependentDeductionFromDate),
        },
        {
            title: t("relatives.dependentDeductionToDate"),
            field: "dependentDeductionToDate",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => getDate(rowData?.dependentDeductionToDate),
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSetSelectListStaffFamilyRelationship}
            data={staffFamilyRelationshipList}
            columns={columns}
            nonePagination
        />
    );
});
