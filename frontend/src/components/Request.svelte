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
                    Accept
                </Button>

                <Button pill class="!p-2" outline color="red"
                        on:click={() => updateRequest("DECLINED")}>
                    Decline
                </Button>

            </div>
        </TableBodyCell>
    {/if}
</TableBodyRow>
