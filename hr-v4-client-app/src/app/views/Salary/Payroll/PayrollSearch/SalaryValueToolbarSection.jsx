import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useHistory } from "react-router-dom";
import { Button, Tooltip, Menu, MenuItem } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import BorderAllIcon from '@material-ui/icons/BorderAll';
import ExtensionIcon from '@material-ui/icons/Extension';

function SalaryValueToolbarSection() {
    const { t } = useTranslation();
    const history = useHistory();

    const {
        payrollStore
    } = useStore();

    const {
        handleImportFileSalaryValueByFilter,
        handleExportFileImportSalaryValueByFilter,
        listSalaryResultStaffs,
        pagingSalaryResultStaff,
        getListSumSalaryResultStaff
    } = payrollStore;

    const isDisabledExport = !listSalaryResultStaffs?.length;


    const [anchorEl, setAnchorEl] = useState(null);

    const handleOpenMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleCloseMenu = () => {
        setAnchorEl(null);
    };

    async function handleImportFile(e) {
        const isSuccess = await handleImportFileSalaryValueByFilter(e);
        if (isSuccess) {
            await pagingSalaryResultStaff();
            await getListSumSalaryResultStaff();

        }
    }

    return (
        <>
            <Tooltip
                arrow
                placement='top'
                title="Thao tác với các giá trị trong bảng lương"
            >
                <Button
                    className="d-inline-flex btnHrStyle "
                    startIcon={<ExtensionIcon />}
                    onClick={handleOpenMenu}
                    disabled={isDisabledExport}
                >
                    <span className="font-size-14">
                        Giá trị lương
                    </span>
                </Button>
            </Tooltip>


            {
                Boolean(anchorEl) && (
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={handleCloseMenu}
                        anchorOrigin={{
                            vertical: "bottom", // Menu sẽ bắt đầu từ phía dưới của nút
                            horizontal: "left", // Căn trái với nút
                        }}
                        transformOrigin={{
                            vertical: "top", // Điểm gốc của menu là phía trên
                            horizontal: "left", // Căn trái
                        }}
                    >
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={() => {
                                handleExportFileImportSalaryValueByFilter(true);
                                handleCloseMenu();
                            }}
                        // disabled={isDisabledExport}
                        >
                            Tải mẫu nhập giá trị lương
                        </MenuItem>

                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={() => {
                                const inputel = document.getElementById("fileExcelSalaryValue");

                                document.getElementById("fileExcelSalaryValue").click();
                                handleCloseMenu();
                            }}
                        // disabled={isDisabledExport}
                        >
                            Nhập giá trị lương từ mẫu
                        </MenuItem>

                    </Menu>
                )
            }

            <input
                type='file'
                id='fileExcelSalaryValue'
                style={{ display: "none" }}
                onChange={(e) => {
                    //handleImportFileSalaryValueByFilter(e);
                    handleImportFile(e);
                }}
            />
        </>


    );
}

export default memo(observer(SalaryValueToolbarSection));