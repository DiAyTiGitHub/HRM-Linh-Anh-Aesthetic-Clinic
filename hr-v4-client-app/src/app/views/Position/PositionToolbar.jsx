import { Button , ButtonGroup , Grid , Menu , MenuItem , Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import DeleteIcon from "@material-ui/icons/Delete";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import TransformIcon from "@material-ui/icons/Transform";
import GetAppIcon from "@material-ui/icons/GetApp";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import { memo , useState } from "react";
import { useTranslation } from "react-i18next";
import StaffPositionFilter from "./PositionFilter";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function PositionToolbar() {
    const {positionStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList ,
        handleTransfer ,
        pagingPosition ,
        handleOpenCreateEdit ,
        searchObject ,
        listOnDelete ,
        handleSetSearchObject ,
        uploadFileExcel ,
        handleDownloadPositionTemplate ,
        handleDownloadPositionRelationshipTemplate ,
        handlExportExcelPositionData ,
        uploadPositionRelationShipFileExcel ,
    } = positionStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            pageIndex:1 ,
            pageSize:searchObject.pageSize ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingPosition();
    }

    const [isOpenFilter , setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    const [anchorEl , setAnchorEl] = useState(null);

    const handleOpenMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleCloseMenu = () => {
        setAnchorEl(null);
    };

    const [anchorElInstall , setAnchorElInstall] = useState(null);

    const handleOpenMenuInstall = (event) => {
        setAnchorElInstall(event.currentTarget);
    };

    const handleCloseMenuInstall = () => {
        setAnchorElInstall(null);
    };
    const {isAdmin, isManager} = useStore().hrRoleUtilsStore;

    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({resetForm , values , setFieldValue , setValues}) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} xl={6}>
                                    {(isAdmin || isManager) && (
                                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                                            <Button startIcon={<AddIcon/>} onClick={() => handleOpenCreateEdit()}>
                                                {t("general.button.add")}
                                            </Button>
                                            <Tooltip
                                                placement='top'
                                                title={"Tải xuống Excel danh sách chức vụ theo bộ lọc"}
                                                arrow>
                                                <Button
                                                    startIcon={<CloudDownloadIcon/>}
                                                    onClick={() => handlExportExcelPositionData()}>
                                                    Xuất Excel
                                                </Button>
                                            </Tooltip>

                                            <Tooltip placement='top' title='Nhập excel'>
                                                <Button
                                                    startIcon={<CloudUploadIcon fontSize='small'/>}
                                                    onClick={handleOpenMenu}>
                                                    {t("general.button.importExcel")}
                                                </Button>
                                            </Tooltip>
                                            <Menu
                                                anchorEl={anchorEl}
                                                open={Boolean(anchorEl)}
                                                onClose={handleCloseMenu}
                                                anchorOrigin={{
                                                    vertical:"bottom" , // Menu sẽ bắt đầu từ phía dưới của nút
                                                    horizontal:"left" , // Căn trái với nút
                                                }}
                                                transformOrigin={{
                                                    vertical:"top" , // Điểm gốc của menu là phía trên
                                                    horizontal:"left" , // Căn trái
                                                }}>
                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={() => document.getElementById("fileExcel").click()}>
                                                    Nhập vị trí
                                                </MenuItem>
                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={() =>
                                                        document.getElementById("positionRelationshipExcel").click()
                                                    }>
                                                    Nhập quan hệ giữa các vị trí
                                                </MenuItem>
                                            </Menu>

                                            <Tooltip placement='top' title='Tải mẫu nhập'>
                                                <Button
                                                    startIcon={<GetAppIcon fontSize='small'/>}
                                                    onClick={handleOpenMenuInstall}>
                                                    Tải mẫu nhập
                                                </Button>
                                            </Tooltip>
                                            <Menu
                                                anchorEl={anchorElInstall}
                                                open={Boolean(anchorElInstall)}
                                                onClose={handleCloseMenuInstall}
                                                anchorOrigin={{
                                                    vertical:"bottom" , // Menu sẽ bắt đầu từ phía dưới của nút
                                                    horizontal:"left" , // Căn trái với nút
                                                }}
                                                transformOrigin={{
                                                    vertical:"top" , // Điểm gốc của menu là phía trên
                                                    horizontal:"left" , // Căn trái
                                                }}>
                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={handleDownloadPositionTemplate}>
                                                    Tải mẫu nhập vị trí
                                                </MenuItem>
                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={handleDownloadPositionRelationshipTemplate}>
                                                    Tải mẫu nhập quan hệ giữa các vị trí
                                                </MenuItem>
                                            </Menu>

                                            <Button
                                                startIcon={<DeleteIcon/>}
                                                onClick={handleDeleteList}
                                                disabled={!listOnDelete?.length > 0}>
                                                Xóa
                                            </Button>

                                            <Tooltip placement='top' title='Điều chuyển vị trí'>
                                                <Button
                                                    startIcon={<TransformIcon/>}
                                                    onClick={handleTransfer}
                                                    disabled={!listOnDelete?.length > 0}>
                                                    Điều chuyển
                                                </Button>
                                            </Tooltip>
                                        </ButtonGroup>
                                    )}
                                    <input
                                        type='file'
                                        id='fileExcel'
                                        style={{display:"none"}}
                                        onChange={uploadFileExcel}
                                    />
                                    <input
                                        type='file'
                                        id='positionRelationshipExcel'
                                        style={{display:"none"}}
                                        onChange={uploadPositionRelationShipFileExcel}
                                    />
                                </Grid>
                                <Grid item xs={12} xl={6}>
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top' title='Tìm kiếm theo tên vị trí'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm theo tên vị trí...'
                                                name='keyword'
                                                variant='outlined'
                                                notDelay
                                            />
                                        </Tooltip>
                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={handleTogglePopupFilter}>
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <StaffPositionFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(PositionToolbar));
