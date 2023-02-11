<script>
    import {Button, Fileupload, Label, TabItem, Tabs} from "flowbite-svelte";
    import ReviewForm from "./ReviewForm.svelte";
    import MessageBoard from "./MessageBoard.svelte";
    import ConfirmDeletionModal from "./ConfirmDeletionModal.svelte";

    import {onMount} from "svelte";
    import Cookies from "js-cookie";
    import {page} from '$app/stores';
    import PaperView from "./PaperView.svelte";

    export let review = {
        reviewer_id: "",
        entry_id: ""
    }
    export let category = {
        id:""
    }
    export let current_user = {
        id: "",
        first_name: "",
        last_name: "",
        email: "",
        account_management_url: "",
    };
    let reviewerUser = true;
    let path = $page.url.pathname;
    export let pdf_document = null;
    export let error = null;

    let fileInput = null;
    let file;
    let upload_state = "";
    let fileuploadprops = {
        id: "annotations_file_input",
        accept: ".pdf,application/pdf"
    };
    let show_confirm_deletion_modal = false

    function loadReviewDocument() {
        pdf_document = null;
        fetch("/api" + path + "/document")
            .then(resp => resp.blob())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    pdf_document = window.URL.createObjectURL(resp);
                }
            })
            .catch(err => console.log(err))
    }

    function uploadReviewPdf() {
        if (upload_state === "uploading" || upload_state === "done") return;
        upload_state = "uploading";
        const input = document.getElementById(fileuploadprops.id);
        file = input.files[0];
        console.log(file);
        const formData = new FormData();
        formData.append("file", file);
        fetch("/api" + path + "/document", {
            method:"POST",
            body: formData
        })
            .then(resp => resp)
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    upload_state = "done"
                    loadReviewDocument()
                }
            })
            .catch(err => {
                console.log(err)
                upload_state = "failed"
                }
            )
    }

    onMount(() => {
        // get current user data from cookie
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
        if (current_user.id === review.reviewer_id) {
            reviewerUser = true;
        }
        loadReviewDocument()
    })

    $: if (!show_confirm_deletion_modal) {
        loadReviewDocument()
    }
</script>

<div class="flex flex-auto h-full">
    <div class="p-4 w-[100%] mx-5 overflow-auto">
        <Tabs style="underline">
            <TabItem open>
                <div class="flex items-center gap-2" slot="title">
                    <svg aria-hidden="true" class="w-5 h-5" fill="currentColor"
                         style="enable-background:new 0 0 243.317 243.317;" viewBox="0 0 243.317 243.317" x="0px"
                         xml:space="preserve"
                         xmlns="http://www.w3.org/2000/svg" y="0px">
              <path d="M242.949,93.714c-0.882-2.715-3.229-4.694-6.054-5.104l-74.98-10.9l-33.53-67.941c-1.264-2.56-3.871-4.181-6.725-4.181
                      c-2.855,0-5.462,1.621-6.726,4.181L81.404,77.71L6.422,88.61C3.596,89.021,1.249,91,0.367,93.714
                      c-0.882,2.715-0.147,5.695,1.898,7.688l54.257,52.886L43.715,228.96c-0.482,2.814,0.674,5.658,2.983,7.335
                      c2.309,1.678,5.371,1.9,7.898,0.571l67.064-35.254l67.063,35.254c1.097,0.577,2.296,0.861,3.489,0.861c0.007,0,0.014,0,0.021,0
                      c0,0,0,0,0.001,0c4.142,0,7.5-3.358,7.5-7.5c0-0.629-0.078-1.24-0.223-1.824l-12.713-74.117l54.254-52.885
                      C243.096,99.41,243.832,96.429,242.949,93.714z M173.504,146.299c-1.768,1.723-2.575,4.206-2.157,6.639l10.906,63.581
                      l-57.102-30.018c-2.185-1.149-4.795-1.149-6.979,0l-57.103,30.018l10.906-63.581c0.418-2.433-0.389-4.915-2.157-6.639
                      l-46.199-45.031l63.847-9.281c2.443-0.355,4.555-1.889,5.647-4.103l28.55-57.849l28.55,57.849c1.092,2.213,3.204,3.748,5.646,4.103
                      l63.844,9.281L173.504,146.299z"/>
            </svg>
                    Review form
                </div>
                <ReviewForm maxScore="{category.max_score}" minScore="{category.min_score}" reviewerUser="{reviewerUser}"/>
            </TabItem>
            <TabItem>
                <div class="flex items-center gap-2" slot="title">
                    <svg aria-hidden="true" class="w-5 h-5" style="enable-background:new 0 0 87.881 87.881;"
                         viewBox="0 0 87.881 87.881" x="0px"
                         xml:space="preserve" xmlns="http://www.w3.org/2000/svg" y="0px">
                        <g>
                          <path d="M70.828,0H33.056C27.535,0,23.044,4.484,23.03,10.001h-2.975c-7.183,0-13.027,5.844-13.027,13.027v51.826
                            c0,7.184,5.844,13.027,13.027,13.027h37.772c7.183,0,13.026-5.844,13.026-13.027v-2.975c5.517-0.015,10.001-4.506,10.001-10.026
                            V10.027C80.854,4.498,76.356,0,70.828,0z M64.854,30.054v37.774v7.026c0,3.881-3.146,7.027-7.026,7.027H20.055
                            c-3.882,0-7.027-3.146-7.027-7.027V23.028c0-3.881,3.146-7.027,7.027-7.027h37.772c3.88,0,7.026,3.146,7.026,7.027L64.854,30.054
                            L64.854,30.054z M74.854,61.853c0,2.212-1.793,4.011-4.001,4.024V30.054v-7.026c0-7.183-5.844-13.027-13.026-13.027H29.031
                            C29.045,7.793,30.844,6,33.056,6h37.773c2.22,0,4.026,1.807,4.026,4.027V61.853z"/>
                            <rect height="6" width="36" x="20.941" y="27.313"/>
                            <rect height="6" width="36" x="20.941" y="40.187"/>
                            <rect height="6" width="36" x="20.941" y="53.061"/>
                            <rect height="6" width="36" x="20.941" y="65.935"/>
                        </g>
                      </svg>
                    {#if reviewerUser}
                        Upload PDF file
                    {:else}
                        Reviewed PDF file
                    {/if}
                </div>
                <Label class="pb-2">Your PDF</Label>
                <div class="flex flex-row justify-between items-center">
                    {#if reviewerUser}
                        <Fileupload {...fileuploadprops} bind:value={fileInput} inputClass="my-auto annotations_file_input"
                                    on:change={() => upload_state = ""}
                                    size="lg"
                        />
                        <Button disabled={!fileInput}
                                on:click={() => uploadReviewPdf()} outline
                                color={upload_state === "done" ? "green" : (upload_state === "failed" ? "red" : "blue")} >
                            {upload_state === "done" ? "Done" : (upload_state === "failed" ? "Failed" : "Upload")}
                        </Button>
                    {/if}

                </div>
                {#if pdf_document !== null}
                    <div class="absolute flex h-full w-[50%] right-1/4 left-1/4 justify-center">
                        <PaperView document="{pdf_document}"/>
                    </div>
                    <Button on:click={() => show_confirm_deletion_modal = true}>Delete</Button>

                {/if}
            </TabItem>
            <TabItem>
                <div class="flex items-center gap-2" slot="title">
                    <svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"
                         xmlns="http://www.w3.org/2000/svg">
                        <g>
                            <g>
                                <g>
                                    <path d="M20,20h-4c-0.6,0-1-0.4-1-1s0.4-1,1-1h4c1.1,0,2-0.9,2-2V4c0-1.1-0.9-2-2-2H4C2.9,2,2,2.9,2,4v12c0,1.1,0.9,2,2,2h4
				c0.6,0,1,0.4,1,1s-0.4,1-1,1H4c-2.2,0-4-1.8-4-4V4c0-2.2,1.8-4,4-4h16c2.2,0,4,1.8,4,4v12C24,18.2,22.2,20,20,20z"/>
                                </g>
                            </g>
                            <g>
                                <g>
                                    <path d="M12,24c-0.3,0-0.5-0.1-0.7-0.3l-4-4c-0.4-0.4-0.4-1,0-1.4s1-0.4,1.4,0l3.3,3.3l3.3-3.3c0.4-0.4,1-0.4,1.4,0s0.4,1,0,1.4
				l-4,4C12.5,23.9,12.3,24,12,24z"/>
                                </g>
                            </g>
                            <g>
                                <g>
                                    <path d="M18,7H6C5.4,7,5,6.6,5,6s0.4-1,1-1h12c0.6,0,1,0.4,1,1S18.6,7,18,7z"/>
                                </g>
                            </g>
                            <g>
                                <g>
                                    <path d="M13,11H6c-0.6,0-1-0.4-1-1s0.4-1,1-1h7c0.6,0,1,0.4,1,1S13.6,11,13,11z"/>
                                </g>
                            </g>
                            <g>
                                <g>
                                    <path d="M16,15H6c-0.6,0-1-0.4-1-1s0.4-1,1-1h10c0.6,0,1,0.4,1,1S16.6,15,16,15z"/>
                                </g>
                            </g>
                        </g>
                    </svg>
                    Message board
                </div>
                <p class="text-sm text-gray-500 dark:text-gray-400"><b class="text-3xl font-bold text-gray-900">Message
                    board:</b>
                    <MessageBoard review={review} category={category}/>
                </p>
            </TabItem>
        </Tabs>
    </div>
</div>

<ConfirmDeletionModal hide="{() => show_confirm_deletion_modal = false}" show="{show_confirm_deletion_modal}"
                      to_delete={path + "/document"} delete_name="Review Document" afterpath="{path}"/>
