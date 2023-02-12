<script>
    import {
        BreadcrumbItem, Button,
        TableBodyCell,
        TableBodyRow
    } from "flowbite-svelte";
    import {createEventDispatcher} from "svelte";

    export let request;
    export let entry;
    export let pending = false;
    export let accepted = false;
    export let error;

    const dispatch = createEventDispatcher();

    function updateRequest(state) {
        let data = {
            state: state
        };
        fetch("/api/categories/" + entry.category_id + "+/entries/" + entry.id + "/process/requests", {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    dispatch("requestUpdated", state);
                }
            })
            .catch(err => console.log(err))
    }

</script>

<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="categories/{entry.category_id}/entries/{entry.id}">{entry.name}</BreadcrumbItem>

    </TableBodyCell>
    {#if pending}
        <TableBodyCell>
            <div class="justify-center flex w-full gap-x-2">
                <Button pill class="!p-2" outline
                        on:click={() => updateRequest("ACCEPTED")}>
                    <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                         width="32px" height="32px" viewBox="0 0 27 27"
                         xml:space="preserve"
                         fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                </Button>

                <Button pill class="!p-2" outline color="red"
                        on:click={() => updateRequest("DECLINED")}>
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
    {/if}
</TableBodyRow>
