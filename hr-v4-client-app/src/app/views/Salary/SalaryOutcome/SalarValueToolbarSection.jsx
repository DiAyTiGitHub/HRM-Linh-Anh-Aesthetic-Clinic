import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useHistory } from "react-router-dom";
import { Button, Tooltip, Menu, MenuItem } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import BorderAllIcon from '@material-ui/icons/BorderAll';
import ExtensionIcon from '@material-ui/icons/Extension';

function SalarValueToolbarSection() {
    const { t } = useTranslation();
    const history = useHistory();

    const {
        salaryOutcomeStore,
    } = useStore();

    const {
        handleImportFileSalaryValueByFilter,
        handleExportFileImportSalaryValueByFilter,
        onViewSalaryResult
    } = salaryOutcomeStore;

    const isDisabledExport = !onViewSalaryResult?.salaryResultStaffs?.length;


    const [anchorEl, setAnchorEl] = useState(null);

    const handleOpenMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleCloseMenu = () => {
        setAnchorEl(null);
    };


    return (
        <>
            <Tooltip
                arrow
                placement='top'
                title="Xuất Excel danh sách lương theo bộ lọc"
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
                            disabled={isDisabledExport}
                        >
                            Tải mẫu nhập giá trị lương
                        </MenuItem>

                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={() => {
                                handleImportFileSalaryValueByFilter(true);
                                handleCloseMenu();
                            }}
                            disabled={isDisabledExport}
                        >
                            Nhập giá trị lương từ mẫu
                        </MenuItem>
                    </Menu>
                )
            }
        </>


    );
}

export default memo(observer(SalarValueToolbarSection));