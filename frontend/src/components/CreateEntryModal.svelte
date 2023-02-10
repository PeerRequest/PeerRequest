<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        TableBodyRow,
        TableHeadCell,
        TableHead,
        Table,
        TableBody,
        TableBodyCell,
        Chevron,
        Dropdown,
        Search,
        Fileupload,
        Spinner
    } from "flowbite-svelte" ;
    import mock_data from "../mock_data.js";

    let upload_state = "";
    let annotations_file = null;
    let fileuploadprops = {
        id: "annotations_file_input",
        accept: ".pdf,application/pdf"
    };

    function file_upload() {
        if (upload_state === "uploading" || upload_state === "done") return;

        const input = document.getElementById(fileuploadprops.id);
        const file = input.files[0];

        // upload file here
        console.log(file);

        upload_state = "uploading";
        setTimeout(() => {
            upload_state = Math.random() < 0.5 ? "failed" : "done";
        }, 2200);
    }

    export let error = null;
    export let category;
    let entries = null;
    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let query = "";

    let users = mock_data.users;
    let reviewers = [];
    let files;

    function apply_query(q) {
        query = q;
    }

    function addReviewer(u) {
        let inputValOpenSlots = document.getElementById("selected_open_slots").value;
        if (reviewers.length + 1 <= inputValOpenSlots) {
            reviewers = reviewers.concat([u])
        } else {
            alert("Warning! Not enough open slots!")
        }
    }

    let authors;
    let name;
    let category_id = category.id;
    let category_path = `/api/categories/${category_id}/entries`

    let fileInput;
    let file;
    let new_entry_id;

    function createEntry() {
        file = fileInput.files[0];
        console.log(file);
        const formData = new FormData();
        formData.append("authors", authors);
        formData.append("name", name);
        formData.append("file", file);
        return fetch(category_path, {
            method: "POST",

            body: formData
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    try {
                        hide();
                        new_entry_id = resp.content.id;
                    } catch (error) {
                        console.log(error);
                    }
                    sendDirectRequest()
                }
            })
            .catch(err => console.log(err));
    }

    function sendDirectRequest() {

    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create New Entry
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>

    <form class="grid gap-y-6" enctype="multipart/form-data" on:submit|preventDefault={() => createEntry()}>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Enter Paper Title</Heading>
            <input bind:value={name} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_title required
                   type=text>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Enter Paper Authors</Heading>
            <input bind:value={authors} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_authors
                   placeholder="(Optional)" type=text>
        </div>


        <div class="flex flex-row justify-between items-center">
            <input bind:this={fileInput} type="file"/>
        </div>


        <!--
        <div class="flex flex-1 items-center ">
            <Fileupload {...fileuploadprops} bind:value={annotations_file} inputClass="my-auto annotations_file_input"
                        on:change={() => upload_state = ""}
                        size="lg"
            />
            <Button class="m-auto"
                    color={upload_state === "done" ? "green" : (upload_state === "failed" ? "red" : "blue")}
                    disabled={!annotations_file} on:click={file_upload}
                    outline size="md">
                {#if upload_state === "uploading"}
                    <Spinner class="mr-3" size="5"/>
                {:else }
                    {#if upload_state === "done"}
                        <svg class="mr-3" width="15px" height="15px" viewBox="0 0 15 15" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                            <path d="M1 7L5.5 11.5L14 3" stroke="black" stroke-linecap="square"/>
                        </svg>
                    {:else }
                        {#if upload_state === "failed" }
                            <svg class="mr-3" width="15px" height="15px" xmlns="http://www.w3.org/2000/svg" x="0px"
                                 y="0px"
                                 viewBox="0 0 490 490" style="enable-background:new 0 0 490 490;" xml:space="preserve">
                <polygon points="456.851,0 245,212.564 33.149,0 0.708,32.337 212.669,245.004 0.708,457.678 33.149,490 245,277.443 456.851,490
                  489.292,457.678 277.331,245.004 489.292,32.337 "/>
          </svg>
                        {/if}
                    {/if}
                {/if}{upload_state === "done" ? "Done" : (upload_state === "failed" ? "Failed" : "Upload")}
            </Button>
        </div>
        -->


        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Choose The Number Of Review Slots</Heading>
            <input class="justify-end" id=selected_open_slots min={ reviewers.length > 0 ? reviewers.length : 1 }
                   type=number
                   value=1>
        </div>

        <Button color="primary">
            <Chevron>Add Reviewer</Chevron>
        </Button>
        <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
            <div class="p-3" slot="header">
                <Search on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                        size="md"/>
            </div>
            {#each users.filter(u => !reviewers.includes(u) &&
                (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
                <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
                <span class="cursor-pointer" on:click={() => addReviewer(u) }>
                  {u.name}
                </span>
                </li>
            {/each}
        </Dropdown>
    </form>

    <div class="mb-4">
        <Table divClass="relative">
            <TableHead>
                <TableHeadCell>Name</TableHeadCell>
            </TableHead>
            <TableBody class="divide-y">
                {#each reviewers as r }
                    <TableBodyRow>
                        <TableBodyCell>{r.name}</TableBodyCell>
                        <TableBodyCell>
                            <div class="flex flex-wrap items-center gap-2">
                                <Button pill class="!p-2" outline color="red"
                                        on:click={() => reviewers = reviewers.filter(e => e !== r)}>
                                    <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                                         width="32px" height="32px" viewBox="0 0 64 64"
                                         xml:space="preserve">
                          <g>
                            <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="18.947"
                                  y1="17.153" x2="45.045"
                                  y2="43.056"/>
                          </g>
                                        <g>
                            <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="19.045"
                                  y1="43.153" x2="44.947"
                                  y2="17.056"/>
                          </g>
                      </svg>
                                </Button>
                            </div>
                        </TableBodyCell>
                    </TableBodyRow>
                {/each}
            </TableBody>
        </Table>

    </div>
    <Button color="primary" on:click={() => createEntry()} size="sm" type="submit">
        Finish Submission
    </Button>


</Modal>